package org.jetbrains.kotlinx.spark.api.compilerPlugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addDispatchReceiver
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irElseBranch
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIs
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.superTypes
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

class DataClassSparkifyGenerator(
    private val pluginContext: IrPluginContext,
    private val sparkifyAnnotationFqNames: List<String>,
    private val columnNameAnnotationFqNames: List<String>,
    private val productFqNames: List<String>,
) : IrElementVisitorVoid {

    init {
        require(sparkifyAnnotationFqNames.isNotEmpty()) {
            "At least one sparkify annotation must be provided"
        }
        require(columnNameAnnotationFqNames.isNotEmpty()) {
            "At least one column name annotation must be provided"
        }
    }

    override fun visitElement(element: IrElement) {
        when (element) {
//            is IrDeclaration,
//            is IrFile,
//            is IrBlockBody,
//            is IrModuleFragment -> element.acceptChildrenVoid(this)

            // test for now
            else -> element.acceptChildrenVoid(this)
        }
    }

    /**
     * Converts
     * ```kt
     * @Sparkify
     * data class User(
     *     val name: String = "John Doe",
     *     @get:JvmName("ignored") val age: Int = 25,
     *     @ColumnName("a") val test: Double = 1.0,
     *     @get:ColumnName("b") val test2: Double = 2.0,
     * )
     * ```
     * to
     * ```kt
     * @Sparkify
     * data class User(
     *     @get:JvmName("name") val name: String = "John Doe",
     *     @get:JvmName("age") val age: Int = 25,
     *     @get:JvmName("a") @ColumnName("a") val test: Double = 1.0,
     *     @get:JvmName("b") @get:ColumnName("b") val test2: Double = 2.0,
     * )
     * ```
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitProperty(declaration: IrProperty) {
        val origin = declaration.parent as? IrClass ?: return super.visitProperty(declaration)
        if (sparkifyAnnotationFqNames.none { origin.hasAnnotation(FqName(it)) })
            return super.visitProperty(declaration)

        if (!origin.isData) return super.visitProperty(declaration)

        // must be in primary constructor
        val constructorParams = declaration.parentAsClass.primaryConstructor?.valueParameters
            ?: return super.visitProperty(declaration)

        if (declaration.name !in constructorParams.map { it.name })
            return super.visitProperty(declaration)

        val getter = declaration.getter ?: return super.visitProperty(declaration)

        // Let's find if there's a ColumnName annotation
        val columnNameAnnotationFqNames = columnNameAnnotationFqNames.map { FqName(it) }

        val allAnnotations = declaration.annotations +
                getter.annotations +
                constructorParams.first { it.name == declaration.name }.annotations
        val columnNameAnnotation = allAnnotations
            .firstOrNull { annotation ->
                columnNameAnnotationFqNames.any {
                    annotation.isAnnotationWithEqualFqName(it) &&
                            annotation.valueArguments.count {
                                it?.type == pluginContext.irBuiltIns.stringType
                            } >= 1
                }
            }

        // if there is, get the ColumnName value, else use the property name as newName
        val columnName = columnNameAnnotation
            ?.valueArguments
            ?.firstOrNull { it?.type == pluginContext.irBuiltIns.stringType }
            ?.let { it as? IrConst<*> }
            ?.value as? String
        val newName = columnName ?: declaration.name.identifier

        val jvmNameFqName = FqName(JvmName::class.qualifiedName!!)

        // remove previous JvmNames
        getter.annotations = getter.annotations
            .filterNot { it.isAnnotationWithEqualFqName(jvmNameFqName) }

        // create a new JvmName annotation with newName
        val jvmNameClassId = jvmNameFqName.toClassId()
        val jvmName = pluginContext.referenceClass(jvmNameClassId)!!
        val jvmNameConstructor = jvmName
            .constructors
            .firstOrNull()!!

        val jvmNameAnnotationCall = IrConstructorCallImpl.fromSymbolOwner(
            type = jvmName.defaultType,
            constructorSymbol = jvmNameConstructor,
        )
        jvmNameAnnotationCall.putValueArgument(
            index = 0,
            valueArgument = IrConstImpl.string(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = pluginContext.irBuiltIns.stringType,
                value = newName,
            )
        )
        getter.annotations += jvmNameAnnotationCall
        println("Added @get:JvmName(\"$newName\") annotation to property ${origin.name}.${declaration.name}")
    }

    private fun FqName.toClassId(): ClassId = ClassId(packageFqName = parent(), topLevelName = shortName())

    /**
     * Converts
     * ```kt
     * @Sparkify
     * data class User(
     *    val name: String = "John Doe",
     *    val age: Int = 25,
     *    @ColumnName("a") val test: Double = 1.0,
     *    @get:ColumnName("b") val test2: Double = 2.0,
     * )
     * ```
     * to
     * ```kt
     * @Sparkify
     * data class User(
     *    val name: String = "John Doe",
     *    val age: Int = 25,
     *    @ColumnName("a") val test: Double = 1.0,
     *    @get:ColumnName("b") val test2: Double = 2.0,
     * ): scala.Product {
     *   override fun canEqual(that: Any?): Boolean = that is User
     *   override fun productElement(n: Int): Any = when (n) {
     *      0 -> name
     *      1 -> age
     *      2 -> test
     *      else -> throw IndexOutOfBoundsException(n.toString())
     *    }
     *    override fun productArity(): Int = 4
     * }
     * ```
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitClass(declaration: IrClass) {
        if (sparkifyAnnotationFqNames.none { declaration.hasAnnotation(FqName(it)) })
            return super.visitClass(declaration)

        if (!declaration.isData) return super.visitClass(declaration)

        // add superclasses
        val scalaProductClass = productFqNames.firstNotNullOfOrNull {
            val classId = ClassId.topLevel(FqName(it))
            pluginContext.referenceClass(classId)
        }!!

        declaration.superTypes += scalaProductClass.defaultType

        val serializableClass = pluginContext.referenceClass(
            ClassId.topLevel(FqName("java.io.Serializable"))
        )!!

        declaration.superTypes += serializableClass.defaultType

        // finding the constructor params
        val constructorParams = declaration.primaryConstructor?.valueParameters
            ?: return super.visitClass(declaration)

        // finding properties
        val props = declaration.properties

        // getting the properties that are in the constructor
        val properties = constructorParams.mapNotNull { param ->
            props.firstOrNull { it.name == param.name }
        }

        // finding supertype Equals
        val superEqualsInterface = scalaProductClass.superTypes()
            .first { it.classFqName?.shortName()?.asString()?.contains("Equals") == true }
            .classOrNull ?: return super.visitClass(declaration)

        // add canEqual
        val superCanEqualFunction = superEqualsInterface.functions.first {
            it.owner.name.asString() == "canEqual" &&
                    it.owner.valueParameters.size == 1 &&
                    it.owner.valueParameters.first().type == pluginContext.irBuiltIns.anyNType
        }

        val canEqualFunction = declaration.addFunction(
            name = "canEqual",
            returnType = pluginContext.irBuiltIns.booleanType,
            modality = Modality.OPEN,
        )
        with(canEqualFunction) {
            overriddenSymbols = listOf(superCanEqualFunction)
            parent = declaration

            // add implicit $this parameter
            addDispatchReceiver {
                name = SpecialNames.THIS
                type = declaration.defaultType
            }

            // add that parameter
            val that = addValueParameter(
                name = Name.identifier("that"),
                type = pluginContext.irBuiltIns.anyNType,
            )

            // add body
            body = pluginContext.irBuiltIns.createIrBuilder(symbol).irBlockBody {
                val call = irIs(argument = irGet(that), type = declaration.defaultType)
                +irReturn(call)
            }
        }

        // add productArity
        val superProductArityFunction = scalaProductClass.functions.first {
            it.owner.name.asString() == "productArity" &&
                    it.owner.valueParameters.isEmpty()
        }

        val productArityFunction = declaration.addFunction(
            name = "productArity",
            returnType = pluginContext.irBuiltIns.intType,
            modality = Modality.OPEN,
        )
        with(productArityFunction) {
            overriddenSymbols = listOf(superProductArityFunction)
            parent = declaration

            // add implicit $this parameter
            addDispatchReceiver {
                name = SpecialNames.THIS
                type = declaration.defaultType
            }

            // add body
            body = pluginContext.irBuiltIns.createIrBuilder(symbol).irBlockBody {
                val const = properties.size.toIrConst(pluginContext.irBuiltIns.intType)
                +irReturn(const)
            }
        }

        // add productElement
        val superProductElementFunction = scalaProductClass.functions.first {
            it.owner.name.asString() == "productElement" &&
                    it.owner.valueParameters.size == 1 &&
                    it.owner.valueParameters.first().type == pluginContext.irBuiltIns.intType
        }

        val productElementFunction = declaration.addFunction(
            name = "productElement",
            returnType = pluginContext.irBuiltIns.anyNType,
            modality = Modality.OPEN,
        )
        with(productElementFunction) {
            overriddenSymbols = listOf(superProductElementFunction)
            parent = declaration

            // add implicit $this parameter
            val `this` = addDispatchReceiver {
                name = SpecialNames.THIS
                type = declaration.defaultType
            }

            // add n parameter
            val n = addValueParameter(
                name = Name.identifier("n"),
                type = pluginContext.irBuiltIns.intType,
            )

            // add body
            body = pluginContext.irBuiltIns.createIrBuilder(symbol).irBlockBody {
                val whenBranches = buildList {
                    for ((i, prop) in properties.withIndex()) {
                        val condition = irEquals(
                            arg1 = irGet(n),
                            arg2 = i.toIrConst(pluginContext.irBuiltIns.intType),
                        )
                        val call = irCall(prop.getter!!)
                        with(call) {
                            origin = IrStatementOrigin.GET_PROPERTY
                            dispatchReceiver = irGet(`this`)
                        }

                        val branch = irBranch(
                            condition = condition,
                            result = call
                        )
                        add(branch)
                    }

                    val ioobClass = pluginContext.referenceClass(
                        ClassId(FqName("java.lang"), Name.identifier("IndexOutOfBoundsException"))
                    )!!
                    val ioobConstructor = ioobClass.constructors.first { it.owner.valueParameters.isEmpty() }
                    val throwCall = irThrow(
                        IrConstructorCallImpl.fromSymbolOwner(
                            ioobClass.defaultType,
                            ioobConstructor
                        )
                    )
                    val elseBranch = irElseBranch(throwCall)
                    add(elseBranch)
                }
                val whenBlock = irWhen(pluginContext.irBuiltIns.anyNType, whenBranches)
                with(whenBlock) {
                    origin = IrStatementOrigin.IF
                }
                +irReturn(whenBlock)
            }
        }

        // pass down to the properties
        declaration.acceptChildrenVoid(this)
    }
}
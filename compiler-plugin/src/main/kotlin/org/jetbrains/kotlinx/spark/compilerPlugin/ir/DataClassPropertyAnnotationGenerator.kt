package org.jetbrains.kotlinx.spark.compilerPlugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

class DataClassPropertyAnnotationGenerator(
    private val pluginContext: IrPluginContext,
    private val sparkifyAnnotationFqNames: List<String>,
    private val columnNameAnnotationFqNames: List<String>,
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
            is IrDeclaration,
            is IrFile,
            is IrModuleFragment -> element.acceptChildrenVoid(this)
        }
    }

    override fun visitProperty(declaration: IrProperty) {
        val origin = declaration.parent as? IrClass ?: return super.visitProperty(declaration)
        if (sparkifyAnnotationFqNames.none { origin.hasAnnotation(FqName(it)) })
            return super.visitProperty(declaration)

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
        val jvmNameClassId = ClassId(jvmNameFqName.parent(), jvmNameFqName.shortName())
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
}
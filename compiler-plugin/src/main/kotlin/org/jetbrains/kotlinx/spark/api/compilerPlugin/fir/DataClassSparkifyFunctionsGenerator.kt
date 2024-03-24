package org.jetbrains.kotlinx.spark.api.compilerPlugin.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.utils.isData
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.toClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class DataClassSparkifyFunctionsGenerator(
    session: FirSession,
    private val sparkifyAnnotationFqNames: List<String>,
    private val productFqNames: List<String>,
) : FirDeclarationGenerationExtension(session) {

    companion object {
        fun builder(
            sparkifyAnnotationFqNames: List<String>,
            productFqNames: List<String>
        ): (FirSession) -> FirDeclarationGenerationExtension = {
            DataClassSparkifyFunctionsGenerator(
                session = it,
                sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
                productFqNames = productFqNames,
            )
        }

        // functions to generate
        val canEqual = Name.identifier("canEqual")
        val productElement = Name.identifier("productElement")
        val productArity = Name.identifier("productArity")
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val owner = context?.owner ?: return emptyList()

        val functionName = callableId.callableName
        val superTypes = owner.getSuperTypes(session)
        val superProduct = superTypes.first {
            it.toString().endsWith("Product")
        }.toClassSymbol(session)!!
        val superEquals = superTypes.first {
            it.toString().endsWith("Equals")
        }.toClassSymbol(session)!!

        val function = when (functionName) {
            canEqual -> {
                val func = createMemberFunction(
                    owner = owner,
                    key = Key,
                    name = functionName,
                    returnType = session.builtinTypes.booleanType.type,
                ) {
                    valueParameter(
                        name = Name.identifier("that"),
                        type = session.builtinTypes.nullableAnyType.type,
                    )
                }
//                val superFunction = superEquals.declarationSymbols.first {
//                    it is FirNamedFunctionSymbol && it.name == functionName
//                } as FirNamedFunctionSymbol
//                overrides(func, superFunction)
                func
            }

            productElement -> {
                createMemberFunction(
                    owner = owner,
                    key = Key,
                    name = functionName,
                    returnType = session.builtinTypes.nullableAnyType.type,
                ) {
                    valueParameter(
                        name = Name.identifier("n"),
                        type = session.builtinTypes.intType.type,
                    )
                }
            }

            productArity -> {
                createMemberFunction(
                    owner = owner,
                    key = Key,
                    name = functionName,
                    returnType = session.builtinTypes.intType.type,
                )
            }

            else -> {
                return emptyList()
            }
        }

        return listOf(function.symbol)
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        if (classSymbol.isData && classSymbol.annotations.any { "Sparkify" in it.render() }) {
            setOf(canEqual, productElement, productArity)
        } else {
            emptySet()
        }

    object Key : GeneratedDeclarationKey()
}
package org.jetbrains.kotlinx.spark.api.compilerPlugin.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.utils.isData
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.symbols.impl.ConeClassLikeLookupTagImpl
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

/**
 * This class tells the FIR that all @Sparkify annotated data classes
 * get [scala.Product] as their super type.
 */
class DataClassSparkifySuperTypeGenerator(
    session: FirSession,
    private val sparkifyAnnotationFqNames: List<String>,
    private val productFqNames: List<String>,
) : FirSupertypeGenerationExtension(session) {

    companion object {
        fun builder(sparkifyAnnotationFqNames: List<String>, productFqNames: List<String>): (FirSession) -> FirSupertypeGenerationExtension = {
            DataClassSparkifySuperTypeGenerator(
                session = it,
                sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
                productFqNames = productFqNames,
            )
        }
    }

    context(TypeResolveServiceContainer)
    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>
    ): List<FirResolvedTypeRef> = listOf(
        buildResolvedTypeRef {
            val scalaProduct = productFqNames.first().let {
                ClassId.topLevel(FqName(it))
            }
            type = ConeClassLikeTypeImpl(
                lookupTag = ConeClassLikeLookupTagImpl(scalaProduct),
                typeArguments = emptyArray(),
                isNullable = false,
            )
        }

    )

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean =
        declaration.symbol.isData &&
                declaration.annotations.any {
                    "Sparkify" in it.render()
                }
}
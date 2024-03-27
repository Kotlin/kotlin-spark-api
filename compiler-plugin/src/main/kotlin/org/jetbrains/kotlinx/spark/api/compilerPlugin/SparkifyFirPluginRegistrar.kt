package org.jetbrains.kotlinx.spark.api.compilerPlugin

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlinx.spark.api.compilerPlugin.fir.DataClassSparkifyFunctionsGenerator
import org.jetbrains.kotlinx.spark.api.compilerPlugin.fir.DataClassSparkifySuperTypeGenerator

// Potential future K2 FIR hook
// TODO
class SparkifyFirPluginRegistrar(
    private val sparkifyAnnotationFqNames: List<String>,
    private val productFqNames: List<String>
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +DataClassSparkifySuperTypeGenerator.builder(
            sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
            productFqNames = productFqNames,
        )
        +DataClassSparkifyFunctionsGenerator.builder(
            sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
            productFqNames = productFqNames,
        )
    }
}

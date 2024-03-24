package org.jetbrains.kotlinx.spark.api.compilerPlugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlinx.spark.api.compilerPlugin.ir.DataClassPropertyAnnotationGenerator

class SparkifyIrGenerationExtension(
    private val sparkifyAnnotationFqNames: List<String>,
    private val columnNameAnnotationFqNames: List<String>,
    private val productFqNames: List<String>,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val visitors = listOf(
            DataClassPropertyAnnotationGenerator(
                pluginContext = pluginContext,
                sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
                columnNameAnnotationFqNames = columnNameAnnotationFqNames,
                productFqNames = productFqNames,
            ),
        )
        for (visitor in visitors) {
            moduleFragment.acceptChildrenVoid(visitor)
        }
    }
}

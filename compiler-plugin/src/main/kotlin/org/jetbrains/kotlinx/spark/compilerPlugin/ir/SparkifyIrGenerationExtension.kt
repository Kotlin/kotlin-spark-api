package org.jetbrains.kotlinx.spark.compilerPlugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

class SparkifyIrGenerationExtension(
    private val sparkifyAnnotationFqNames: List<String>,
    private val columnNameAnnotationFqNames: List<String>,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val visitors = listOf(
            DataClassPropertyAnnotationGenerator(
                pluginContext = pluginContext,
                sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
                columnNameAnnotationFqNames = columnNameAnnotationFqNames,
            ),
        )
        for (visitor in visitors) {
            moduleFragment.acceptChildrenVoid(visitor)
        }
    }
}

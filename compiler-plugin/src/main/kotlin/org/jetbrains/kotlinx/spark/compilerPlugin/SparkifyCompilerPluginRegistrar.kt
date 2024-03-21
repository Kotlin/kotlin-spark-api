package org.jetbrains.kotlinx.spark.compilerPlugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlinx.spark.Artifacts
import org.jetbrains.kotlinx.spark.compilerPlugin.ir.SparkifyIrGenerationExtension

open class SparkifyCompilerPluginRegistrar: CompilerPluginRegistrar() {
    init {
        println("SparkifyCompilerPluginRegistrar loaded")
    }

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration.get(KEY_ENABLED) != true) return

        val sparkifyAnnotationFqNames = configuration.get(KEY_SPARKIFY_ANNOTATION_FQ_NAMES)
            ?: listOf(Artifacts.defaultSparkifyFqName)

        val columnNameAnnotationFqNames = configuration.get(KEY_COLUMN_NAME_ANNOTATION_FQ_NAMES)
            ?: listOf(Artifacts.defaultColumnNameFqName)

        IrGenerationExtension.registerExtension(
            SparkifyIrGenerationExtension(
                sparkifyAnnotationFqNames = sparkifyAnnotationFqNames,
                columnNameAnnotationFqNames = columnNameAnnotationFqNames,
            )
        )
    }
}

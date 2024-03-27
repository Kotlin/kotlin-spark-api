package org.jetbrains.kotlinx.spark.api.compilerPlugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlinx.spark.api.Artifacts

open class SparkifyCommandLineProcessor : CommandLineProcessor {

    init {
        println("SparkifyCommandLineProcessor loaded")
    }

    override val pluginId: String = Artifacts.compilerPluginId

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        OPTION_ENABLED,
        OPTION_SPARKIFY_ANNOTATION_FQ_NAMES,
        OPTION_COLUMN_NAME_ANNOTATION_FQ_NAMES,
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (val optionName = option.optionName) {
            OPTION_ENABLED.optionName ->
                configuration.put(KEY_ENABLED, value.toBoolean())

            OPTION_SPARKIFY_ANNOTATION_FQ_NAMES.optionName ->
                configuration.put(KEY_SPARKIFY_ANNOTATION_FQ_NAMES, value.split(",").map { it.trim() })

            OPTION_COLUMN_NAME_ANNOTATION_FQ_NAMES.optionName ->
                configuration.put(KEY_COLUMN_NAME_ANNOTATION_FQ_NAMES, value.split(",").map { it.trim() })

            else -> error("Unexpected option: $optionName")
        }
    }
}

internal val KEY_ENABLED = CompilerConfigurationKey<Boolean>("Whether to enable Sparkify")

internal val OPTION_ENABLED = CliOption(
    optionName = "enabled",
    valueDescription = "<true|false>",
    description = "Whether to enable Sparkify",
    required = false,
    allowMultipleOccurrences = false,
)

internal val KEY_SPARKIFY_ANNOTATION_FQ_NAMES = CompilerConfigurationKey<List<String>>(
    "Fully qualified names of annotations for Sparkify"
)

internal val OPTION_SPARKIFY_ANNOTATION_FQ_NAMES = CliOption(
    optionName = "sparkifyAnnotationFqNames",
    valueDescription = "<fqName1,fqName2,...>",
    description = "Fully qualified names of annotations to sparkify",
    required = false,
    allowMultipleOccurrences = false,
)

internal val KEY_COLUMN_NAME_ANNOTATION_FQ_NAMES = CompilerConfigurationKey<List<String>>(
    "Fully qualified names of annotations for ColumnName"
)

internal val OPTION_COLUMN_NAME_ANNOTATION_FQ_NAMES = CliOption(
    optionName = "columnNameAnnotationFqNames",
    valueDescription = "<fqName1,fqName2,...>",
    description = "Fully qualified names of annotations for ColumnName",
    required = false,
    allowMultipleOccurrences = false,
)
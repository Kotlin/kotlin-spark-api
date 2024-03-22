package org.jetbrains.kotlinx.spark.api.compilerPlugin

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

// Potential future K2 FIR hook
class SimplePluginRegistrar(private val sparkifyAnnotationFqNames: List<String>) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {}
}

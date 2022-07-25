package org.jetbrains.kotlinx.spark.api.jupyter

//class JupyterConfiguration(init: JupyterConfiguration.() -> Unit = {}) {
//    init { invoke(init) }
//    val sparkProps: MutableMap<String, Any> = mutableMapOf()
//    operator fun invoke(block: JupyterConfiguration.() -> Unit): JupyterConfiguration {
//        block(this)
//        return this
//    }
//}

interface JupyterConfiguration {
    val sparkProps: MutableMap<String, Any?>

    operator fun invoke(block: JupyterConfiguration.() -> Unit): JupyterConfiguration {
        block(this)
        return this
    }
}
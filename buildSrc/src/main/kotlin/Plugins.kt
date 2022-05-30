import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

inline val PluginDependenciesSpec.kotlin
    get() = kotlin("jvm") version Versions.kotlin

inline val PluginDependenciesSpec.dokka
    get() = id("org.jetbrains.dokka") version Versions.dokka

inline val PluginDependenciesSpec.license
    get() = id("com.github.hierynomus.license") version Versions.licenseGradlePluginVersion

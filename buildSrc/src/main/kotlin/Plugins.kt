import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

inline val PluginDependenciesSpec.shadow
    get() = id("com.github.johnrengelman.shadow") version Versions.shadow

inline val PluginDependenciesSpec.kotlin
    get() = kotlin("jvm") version Versions.kotlin

inline val PluginDependenciesSpec.dokka
    get() = id("org.jetbrains.dokka") version Versions.dokka

inline val PluginDependenciesSpec.license
    get() = id("com.github.hierynomus.license") version Versions.licenseGradlePluginVersion

inline val PluginDependenciesSpec.jcp
    get() = id("com.igormaznitsa.jcp")

inline val DependencyHandlerScope.jcp
    get() = "com.igormaznitsa:jcp:${Versions.jcp}"

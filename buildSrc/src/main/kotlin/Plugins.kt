import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.*
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

inline val DependencyHandlerScope.mavenPublish
    get() = "com.vanniktech:gradle-maven-publish-plugin:${Versions.mavenPublish}"

inline val PluginDependenciesSpec.mavenPublish
    get() = id("com.vanniktech.maven.publish")

inline val PluginDependenciesSpec.mavenPublishBase
    get() = id("com.vanniktech.maven.publish.base")
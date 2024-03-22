import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.PluginDependenciesSpec

inline val PluginDependenciesSpec.kotlinSparkApi
    get() = id("org.jetbrains.kotlinx.spark.api")

inline val PluginDependenciesSpec.kotlin
    get() = kotlin("jvm")

inline val PluginDependenciesSpec.dokka
    get() = id("org.jetbrains.dokka")

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

inline val Project.mavenPublishBase
    get() = "com.vanniktech.maven.publish.base"

inline val PluginDependenciesSpec.jupyter
    get() = kotlin("jupyter.api") version Versions.jupyter

inline val PluginDependenciesSpec.buildconfig
    get() = id("com.github.gmazzo.buildconfig")

inline val PluginDependenciesSpec.gradlePublishPlugin
    get() = id("com.gradle.plugin-publish") version Versions.gradlePublishPlugin

inline val PluginDependenciesSpec.shadow
    get() = id("com.github.johnrengelman.shadow") version Versions.shadow

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.PluginDependenciesSpec


inline val PluginDependenciesSpec.kotlin
    get() = kotlin("jvm")

inline val PluginDependenciesSpec.dokka
    get() = id("org.jetbrains.dokka")

inline val PluginDependenciesSpec.license
    get() = id("com.github.hierynomus.license") version Versions.licenseGradlePluginVersion

inline val PluginDependenciesSpec.jcp
    get() = id("com.igormaznitsa.jcp")

inline val PluginDependenciesSpec.serialization
    get() = kotlin("plugin.serialization")

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


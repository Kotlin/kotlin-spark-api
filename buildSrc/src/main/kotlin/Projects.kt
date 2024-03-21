@file:Suppress("NOTHING_TO_INLINE")

import org.gradle.api.Project

object Projects {

    inline fun Project.searchProject(name: String): Project =
        rootProject
            .childProjects
            .filterKeys { name in it }
            .entries
            .singleOrNull()
            ?.value ?: error("Project $name not found")

    inline val Project.kotlinSparkApi
        get() = searchProject("kotlin-spark-api")

    inline val Project.scalaHelpers
        get() = searchProject("scala-helpers")

    inline val Project.examples
        get() = searchProject("examples")

    inline val Project.jupyter
        get() = searchProject("jupyter")

    inline val Project.scalaTuplesInKotlin
        get() = searchProject("scala-tuples-in-kotlin")

    inline val Project.compilerPlugin
        get() = searchProject("compiler-plugin")

    inline val Project.gradlePlugin
        get() = searchProject("gradle-plugin")
}
package org.jetbrains.kotlinx.spark.api.gradlePlugin

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.jetbrains.kotlinx.spark.api.Artifacts
import javax.inject.Inject

abstract class SparkKotlinCompilerExtension @Inject constructor(project: Project) {

    val enabled: Property<Boolean> = project
        .objects
        .property(Boolean::class.javaObjectType)
        .convention(true)

    val sparkifyAnnotationFqNames: ListProperty<String> = project
        .objects
        .listProperty(String::class.java)
        .convention(listOf(Artifacts.defaultSparkifyFqName))

    val columnNameAnnotationFqNames: ListProperty<String> = project
        .objects
        .listProperty(String::class.java)
        .convention(listOf(Artifacts.defaultColumnNameFqName))

    val outputDir: DirectoryProperty = project
        .objects
        .directoryProperty()
        .convention(project.layout.buildDirectory.dir("generated/sources/sparkKotlinCompilerPlugin"))
}
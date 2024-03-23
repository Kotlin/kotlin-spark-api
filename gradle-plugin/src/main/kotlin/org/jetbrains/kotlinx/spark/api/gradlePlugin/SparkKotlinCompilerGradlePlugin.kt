package org.jetbrains.kotlinx.spark.api.gradlePlugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlinx.spark.api.Artifacts

class SparkKotlinCompilerGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("kotlinSparkApi", SparkKotlinCompilerExtension::class.java, target)

        target.afterEvaluate {
            it.extensions.findByType<KotlinJvmProjectExtension>()?.apply {
                compilerOptions {
                    // Make sure the parameters of data classes are visible to scala
                    javaParameters.set(true)

                    // Avoid NotSerializableException by making lambdas serializable
                    freeCompilerArgs.add("-Xlambdas=class")
                }
            }
        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val target = kotlinCompilation.target.name
        val sourceSetName = kotlinCompilation.defaultSourceSet.name

        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(SparkKotlinCompilerExtension::class.java)

        val enabled = extension.enabled.get()
        val sparkifyAnnotationFqNames = extension.sparkifyAnnotationFqNames.get()
        val columnNameAnnotationFqNames = extension.columnNameAnnotationFqNames.get()

        val outputDir = extension.outputDir.get().dir("$target/$sourceSetName/kotlin")
        kotlinCompilation.defaultSourceSet.kotlin.srcDir(outputDir.asFile)

        val provider = project.provider {
            listOf(
                SubpluginOption(key = "enabled", value = enabled.toString()),
                SubpluginOption(key = "sparkifyAnnotationFqNames", value = sparkifyAnnotationFqNames.joinToString()),
                SubpluginOption(key = "columnNameAnnotationFqNames", value = columnNameAnnotationFqNames.joinToString()),
            )
        }
        return provider
    }

    override fun getCompilerPluginId() = Artifacts.compilerPluginId

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = Artifacts.groupId,
            artifactId = Artifacts.compilerPluginArtifactId,
            version = Artifacts.projectVersion,
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}



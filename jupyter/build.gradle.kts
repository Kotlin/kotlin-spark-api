@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    scala
    kotlin
//    dokka
    mavenPublishBase
    jupyter
    jcp
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.processJupyterApiResources {
    libraryProducers = listOf(
        "org.jetbrains.kotlinx.spark.api.jupyter.SparkIntegration",
        "org.jetbrains.kotlinx.spark.api.jupyter.SparkStreamingIntegration",
    )
}

dependencies {
    with(Projects) {
        api(
            kotlinSparkApi,
        )
    }

    with(Dependencies) {
        api(
            kotlinxHtml,
            sparkSql,
            sparkRepl,
            sparkStreaming,
            hadoopClient,
        )

        implementation(
            kotlinStdLib,
        )

        testImplementation(
            kotest,
            kotlinScriptingCommon,
            kotlinScriptingJvm,
        )

    }
}

tasks.preprocess {
    sources.set(
        listOf(File("./src/main/kotlin"))
            .also { println("srcDirs set to preprocess: $it") }
    )
    clearTarget.set(true)
    target.set(File("./build-preprocessed"))
    fileExtensions.set(listOf("java", "kt"))
    vars.set(
        mapOf(
            "kotlin" to Versions.kotlin,
            "scala" to Versions.scala,
            "scalaCompat" to Versions.scalaCompat,
            "spark" to Versions.spark,
            "sparkMinor" to Versions.sparkMinor,
        )
    )
}

//val changeSourceFolder = task("changeSourceFolder") {
//    sourceSets.main.get().allSource
//        .setSrcDirs(
//            listOf(tasks.preprocess.get().target.get())
//                .also { println("srcDirs set to kotlin: $it") }
//        )
//}.dependsOn(tasks.preprocess)

// TODO for tests

tasks.compileKotlin
    .get()
    .dependsOn(tasks.preprocess)

mavenPublishing {
    configure(KotlinJvm(/*TODO Dokka("dokkaHtml")*/))
}
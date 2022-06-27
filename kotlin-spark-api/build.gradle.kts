@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    kotlin
    dokka

    mavenPublishBase
    jcp
}

group = Versions.groupID
version = Versions.project


repositories {
    mavenCentral()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {

    with(Projects) {
        api(
            core,
            scalaTuplesInKotlin,
        )
    }

    with(Dependencies) {
        implementation(
            kotlinStdLib,
            reflect,
            sparkSql,
            sparkStreaming,
            hadoopClient,
        )

        testImplementation(
            sparkStreamingKafka,
            kotest,
            kotestTestcontainers,
            klaxon,
            atrium,
            sparkStreaming,
            kafkaStreamsTestUtils,
            sparkMl,
        )
    }
}



tasks.preprocess {
    sources.set(
        listOf(File("./src/main/kotlin"))
            .also { println("srcDirs set to preprocess: $it") }
    )
    clearTarget.set(true)
//    verbose.set(true)
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


val changeSourceFolder = task("changeSourceFolder") {
    sourceSets.main {
        java.srcDirs(
            tasks.preprocess.get()
                .target.get()
                .also { println("srcDirs set to kotlin: $it") }
        )
    }
}.dependsOn(tasks.preprocess)

// TODO for tests

tasks.compileKotlin
    .get()
    .dependsOn(changeSourceFolder)

mavenPublishing {
    configure(KotlinJvm(/* TODO Dokka("dokkaHtml") */))
}

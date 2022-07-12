@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    scala
    kotlin
    dokka
    mavenPublishBase
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
    with(Dependencies) {
        implementation(
            kotlinStdLib,
            scalaLibrary,
        )
        testImplementation(
            kotest,
            atrium,
            kotlinTest,
        )
    }
}

tasks.withType<AbstractDokkaLeafTask> {
    dokkaSourceSets {
        create("scala-tuples-in-kotlin") {
            sourceRoot(
                kotlin.sourceSets
                    .main.get()
                    .kotlin
                    .srcDirs
                    .first { it.path.endsWith("kotlin") }
            )
        }
    }
}

mavenPublishing {
    configure(KotlinJvm(Dokka("dokkaHtml")))
}


// Publishing of scala-tuples-in-kotlin can be skipped since it's only dependent on the Scala version
val skipScalaTuplesInKotlin = System.getProperty("skipScalaTuplesInKotlin").toBoolean()
tasks
    .filter { "publish" in it.name }
    .forEach { it.onlyIf { !skipScalaTuplesInKotlin } }


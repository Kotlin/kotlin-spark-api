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
        create("main") {
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

val skipScalaTuplesInKotlin = System.getProperty("skipScalaTuplesInKotlin").toBoolean()
tasks.filter { "publish" in it.name }.forEach {
    it.onlyIf { !skipScalaTuplesInKotlin }
}

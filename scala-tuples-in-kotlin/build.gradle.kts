@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm

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

mavenPublishing {
    configure(KotlinJvm(/* TODO Dokka("dokkaHtml") */))
}

val skipScalaTuplesInKotlin = System.getProperty("skipScalaTuplesInKotlin").toBoolean()
tasks.filter { "publish" in it.name }.forEach {
    it.onlyIf { !skipScalaTuplesInKotlin }
}

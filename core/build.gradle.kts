@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Javadoc

plugins {
    scala
    `java-library`
    jcp

    mavenPublishBase
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

dependencies {

    with(Dependencies) {
        api(
            scalaLibrary,
            reflect,
        )

        implementation(
            sparkSql,
        )
    }
}

tasks.preprocess {
    sources.set(
        listOf(File("./src/main/scala"))
            .also { println("srcDirs set to preprocess: $it") }
    )
    clearTarget.set(true)
    target.set(File("./build-preprocessed"))
    fileExtensions.set(listOf("java", "scala"))
    vars.set(
        mapOf(
            "scala" to Versions.scala,
            "scalaCompat" to Versions.scalaCompat,
            "spark" to Versions.spark,
            "sparkMinor" to Versions.sparkMinor,
        )
    )
}

val changeSourceFolder = task("changeSourceFolder") {
    sourceSets.main
        .get()
        .scala
        .setSrcDirs(
            listOf(tasks.preprocess.get().target.get())
                .also { println("srcDirs set to scala: $it") }
        )
}.dependsOn(tasks.preprocess)


tasks.compileScala
    .get()
    .dependsOn(changeSourceFolder)

mavenPublishing {
    configure(JavaLibrary(Javadoc()))
}

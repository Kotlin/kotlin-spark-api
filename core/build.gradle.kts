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
    sources.set(listOf(File("./src/main/scala")))
    clearTarget.set(true)
    fileExtensions.set(listOf("java", "scala"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { false }
}

scala {
    sourceSets.main {
        scala.setSrcDirs(
            listOf(
                tasks.preprocess.get()
                    .target.get()
            )
        )
    }
}

tasks.compileScala
    .get()
    .dependsOn(tasks.preprocess)

mavenPublishing {
    configure(JavaLibrary(Javadoc()))
}

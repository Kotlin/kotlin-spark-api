@file:Suppress("UnstableApiUsage", "NOTHING_TO_INLINE")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavaLibrary
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

// Setup preprocessing with JCP

inline fun JcpTask.setup(scalaSources: FileCollection) {
    sources.set(scalaSources)
    clearTarget.set(true)
    fileExtensions.set(listOf("scala"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { target.get().exists() }
}

inline fun ScalaCompile.setupWithJcp(preprocess: JcpTask, scalaSources: FileCollection) {
    dependsOn(preprocess)
    outputs.upToDateWhen {
        preprocess.outcomingFiles.files.isEmpty()
    }

    doFirst {
        scala {
            sourceSets {
                main {
                    scala.setSrcDirs(listOf(preprocess.target.get()))
                }
            }
        }
    }

    doLast {
        scala {
            sourceSets {
                main {
                    scala.setSrcDirs(scalaSources)
                }
            }
        }
    }
}

val scalaMainSources = sourceSets.main.get().scala.sourceDirectories

val preprocessMain by tasks.creating(JcpTask::class) {
    setup(scalaMainSources)
}

tasks.compileScala {
    setupWithJcp(preprocessMain, scalaMainSources)
}

mavenPublishing {
    configure(JavaLibrary(Javadoc()))
}


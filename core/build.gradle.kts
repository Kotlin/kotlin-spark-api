@file:Suppress("UnstableApiUsage")

import com.igormaznitsa.jcp.gradle.JcpTask
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

val scalaMainSources = sourceSets.main.get().scala.sourceDirectories

val preprocessMain by tasks.creating(JcpTask::class)  {
    sources.set(scalaMainSources)
    clearTarget.set(true)
    fileExtensions.set(listOf("scala"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { target.get().exists() }
}

tasks.compileScala {
    dependsOn(preprocessMain)
    outputs.upToDateWhen {
        preprocessMain.outcomingFiles.files.isEmpty()
    }
    doFirst {
        scala {
            sourceSets {
                main {
                    scala.setSrcDirs(listOf(preprocessMain.target.get()))
                }
            }
        }
    }

    doLast {
        scala {
            sourceSets {
                main {
                    scala.setSrcDirs(scalaMainSources)
                }
            }
        }
    }
}

mavenPublishing {
    configure(JavaLibrary(Javadoc()))
}


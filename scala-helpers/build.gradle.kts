@file:Suppress("UnstableApiUsage")

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

        // https://github.com/FasterXML/jackson-bom/issues/52
        if (Versions.spark == "3.3.1") implementation(jacksonDatabind)

        implementation(
//            sparkSql, not needed atm
        )
    }
}

java {
    toolchain {
        if (Versions.scalaCompat.toDouble() > 2.12) { // scala 2.12 will always target java 8
            languageVersion.set(
                JavaLanguageVersion.of(Versions.jvmTarget)
            )
        } else if (Versions.jvmTarget == "1.8" || Versions.jvmTarget == "8") {
            languageVersion.set(
                JavaLanguageVersion.of(8)
            )
        }
    }
}

tasks.withType<ScalaCompile> {
    if (Versions.scalaCompat.toDouble() > 2.12) { // scala 2.12 will always target java 8
        targetCompatibility = Versions.jvmTarget
    } else if (Versions.jvmTarget == "1.8" || Versions.jvmTarget == "8") {
        targetCompatibility = "1.8"
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

// Publishing of scala-helpers can be skipped since it's only dependent on the Scala version
val skipScalaOnlyDependent = System.getProperty("skipScalaOnlyDependent").toBoolean()
tasks
    .filter { "publish" in it.name }
    .forEach { it.onlyIf { !skipScalaOnlyDependent } }
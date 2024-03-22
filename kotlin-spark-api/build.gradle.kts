@file:Suppress("UnstableApiUsage")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    kotlin
    dokka
    mavenPublishBase
    jcp
    idea
    kotlinSparkApi // for @Sparkify
}

group = Versions.groupID
version = Versions.project


repositories {
    mavenCentral()
    mavenLocal()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxHeapSize = "8g"
}

dependencies {

    Projects {
        api(
            scalaHelpers,
            scalaTuplesInKotlin
        )
    }

    Dependencies {

        // https://github.com/FasterXML/jackson-bom/issues/52
        if (Versions.spark == "3.3.1") implementation(jacksonDatabind)

        if (Versions.sparkConnect) TODO("unsupported for now")

        implementation(
            kotlinStdLib,
            reflect,
            sparkSql,
            sparkStreaming,
            hadoopClient,
            kotlinDateTime,
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

// Setup preprocessing with JCP for main sources

val kotlinMainSources = kotlin.sourceSets.main.get().kotlin.sourceDirectories

val preprocessMain by tasks.creating(JcpTask::class) {
    sources = kotlinMainSources
    clearTarget = true
    fileExtensions = listOf("kt")
    vars = Versions.versionMap
    outputs.upToDateWhen { target.get().exists() }
}

tasks.compileKotlin {
    dependsOn(preprocessMain)
    outputs.upToDateWhen {
        preprocessMain.outcomingFiles.files.isEmpty()
    }

    doFirst {
        kotlin {
            sourceSets {
                main {
                    kotlin.setSrcDirs(listOf(preprocessMain.target.get()))
                }
            }
        }
    }

    doLast {
        kotlin {
            sourceSets {
                main {
                    kotlin.setSrcDirs(kotlinMainSources)
                }
            }
        }
    }
}

// Setup preprocessing with JCP for test sources

val kotlinTestSources = kotlin.sourceSets.test.get().kotlin.sourceDirectories

val preprocessTest by tasks.creating(JcpTask::class) {
    sources = kotlinTestSources
    clearTarget = true
    fileExtensions = listOf("kt")
    vars = Versions.versionMap
    outputs.upToDateWhen { target.get().exists() }
}

tasks.compileTestKotlin {
    dependsOn(preprocessTest)
    outputs.upToDateWhen {
        preprocessTest.outcomingFiles.files.isEmpty()
    }

    doFirst {
        kotlin {
            sourceSets {
                test {
                    kotlin.setSrcDirs(listOf(preprocessTest.target.get()))
                }
            }
        }
    }

    doLast {
        kotlin {
            sourceSets {
                test {
                    kotlin.setSrcDirs(kotlinTestSources)
                }
            }
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(Versions.jvmTarget)

    }
}

tasks.withType<AbstractDokkaLeafTask> {
    dokkaSourceSets {
        all {
            sourceRoot(preprocessMain.target.get())
        }
    }
}

mavenPublishing {
    configure(KotlinJvm(Dokka("dokkaHtml")))
}

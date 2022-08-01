@file:Suppress("UnstableApiUsage", "NOTHING_TO_INLINE")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin
    dokka
    mavenPublishBase
    jcp
    idea
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

// Setup preprocessing with JCP for main sources

val kotlinMainSources = kotlin.sourceSets.main.get().kotlin.sourceDirectories

val preprocessMain by tasks.creating(JcpTask::class) {
    sources.set(kotlinMainSources)
    clearTarget.set(true)
    fileExtensions.set(listOf("kt"))
    vars.set(Versions.versionMap)
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
    sources.set(kotlinTestSources)
    clearTarget.set(true)
    fileExtensions.set(listOf("kt"))
    vars.set(Versions.versionMap)
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Versions.jvmTarget
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






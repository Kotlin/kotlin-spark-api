@file:Suppress("UnstableApiUsage")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    scala
    kotlin
    dokka
    mavenPublishBase
    jupyter
    jcp
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxHeapSize = "2g"
}

tasks.processJupyterApiResources {
    libraryProducers = listOf(
        "org.jetbrains.kotlinx.spark.api.jupyter.SparkIntegration",
        "org.jetbrains.kotlinx.spark.api.jupyter.SparkStreamingIntegration",
    )
}

dependencies {
    Projects {
        api(
            kotlinSparkApi,
        )
    }

    Dependencies {

        // https://github.com/FasterXML/jackson-bom/issues/52
        if (Versions.spark == "3.3.1") implementation(jacksonDatabind)

        api(
            kotlinxHtml,
            sparkSql,
            sparkRepl,
            sparkStreaming,
            hadoopClient,
        )

        implementation(
            kotlinStdLib,
        )

        testImplementation(
            kotest,
            kotlinScriptingCommon,
            kotlinScriptingJvm,
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
    outputs.upToDateWhen { preprocessMain.outcomingFiles.files.isEmpty() }
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
    fileExtensions = listOf("java", "kt")
    vars = Versions.versionMap
    outputs.upToDateWhen { target.get().exists() }
}

tasks.compileTestKotlin {
    dependsOn(preprocessTest)
    outputs.upToDateWhen { preprocessTest.outcomingFiles.files.isEmpty() }
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
        languageVersion = JavaLanguageVersion.of(Versions.jupyterJvmTarget)
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
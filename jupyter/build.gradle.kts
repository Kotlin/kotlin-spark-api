@file:Suppress("UnstableApiUsage")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
}

tasks.processJupyterApiResources {
    libraryProducers = listOf(
        "org.jetbrains.kotlinx.spark.api.jupyter.SparkIntegration",
        "org.jetbrains.kotlinx.spark.api.jupyter.SparkStreamingIntegration",
    )
}

dependencies {
    with(Projects) {
        api(
            kotlinSparkApi,
        )
    }

    with(Dependencies) {
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

// Setup preprocessing with JCP

fun JcpTask.setup(kotlinSources: FileCollection) {
    sources.set(kotlinSources)
    clearTarget.set(true)
    fileExtensions.set(listOf("kt"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { target.get().exists() }
}

fun KotlinCompile.setupWithJcp(preprocess: JcpTask, kotlinSources: FileCollection) {
    dependsOn(preprocess)
    outputs.upToDateWhen {
        preprocess.outcomingFiles.files.isEmpty()
    }

    doFirst {
        kotlin {
            sourceSets {
                main {
                    kotlin.setSrcDirs(listOf(preprocess.target.get()))
                }
            }
        }
    }

    doLast {
        kotlin {
            sourceSets {
                main {
                    kotlin.setSrcDirs(kotlinSources)
                }
            }
        }
    }
}


val kotlinMainSources = kotlin.sourceSets.main.get().kotlin.sourceDirectories
val kotlinTestSources = kotlin.sourceSets.test.get().kotlin.sourceDirectories

val preprocessMain by tasks.creating(JcpTask::class) {
    setup(kotlinMainSources)
}

val preprocessTest by tasks.creating(JcpTask::class) {
    setup(kotlinTestSources)
}


tasks.compileKotlin {
    setupWithJcp(preprocessMain, kotlinMainSources)
}

tasks.compileTestKotlin {
    setupWithJcp(preprocessTest, kotlinTestSources)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<AbstractDokkaLeafTask> {
    dokkaSourceSets {
        create("jupyter") {
            sourceRoot(preprocessMain.target.get())
        }
    }
}


mavenPublishing {
    configure(KotlinJvm(Dokka("dokkaHtml")))
}
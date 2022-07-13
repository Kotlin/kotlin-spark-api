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

// Setup preprocessing with JCP

inline fun JcpTask.setup(kotlinSources: FileCollection) {
    sources.set(kotlinSources)
    clearTarget.set(true)
    fileExtensions.set(listOf("kt"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { target.get().exists() }
}

inline fun KotlinCompile.setupWithJcp(preprocess: JcpTask, kotlinSources: FileCollection) {
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
        create("kotlin-spark-api") {
            sourceRoot(preprocessMain.target.get())
        }
    }
}

mavenPublishing {
    configure(KotlinJvm(Dokka("dokkaHtml")))
}






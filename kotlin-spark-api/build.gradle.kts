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

val preprocessMain by tasks.creating(JcpTask::class) {
    sources.set(listOf(File("./src/main/kotlin")))
    clearTarget.set(true)
    fileExtensions.set(listOf("java", "kt"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { false }
}

tasks.compileKotlin {
    dependsOn(preprocessMain)
}

val preprocessTest by tasks.creating(JcpTask::class) {
    sources.set(listOf(File("./src/test/kotlin")))
    clearTarget.set(true)
    fileExtensions.set(listOf("java", "kt"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { false }
}

tasks.compileTestKotlin {
    dependsOn(preprocessTest)
}

kotlin {
    sourceSets {
        main {
            kotlin.setSrcDirs(listOf(preprocessMain.target.get()))
        }
        test {
            kotlin.setSrcDirs(listOf(preprocessTest.target.get()))
        }
    }
}

tasks.withType<AbstractDokkaLeafTask> {
    dokkaSourceSets {
        create("main") {
            sourceRoot(preprocessMain.target.get())

        }
    }
}

mavenPublishing {
    configure(KotlinJvm(Dokka("dokkaHtml")))
}

@file:Suppress("UnstableApiUsage")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    scala
    kotlin
//    dokka
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

val preprocessMain by tasks.creating(JcpTask::class) {
    sources.set(listOf(File("./src/main/kotlin")))
    clearTarget.set(true)
    fileExtensions.set(listOf("java", "kt"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { false }
}

tasks.compileKotlin { dependsOn(preprocessMain) }

val preprocessTest by tasks.creating(JcpTask::class) {
    sources.set(listOf(File("./src/test/kotlin")))
    clearTarget.set(true)
    fileExtensions.set(listOf("java", "kt"))
    vars.set(Versions.versionMap)
    outputs.upToDateWhen { false }
}

tasks.compileTestKotlin { dependsOn(preprocessTest) }

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


mavenPublishing {
    configure(KotlinJvm(/*TODO Dokka("dokkaHtml")*/))

}
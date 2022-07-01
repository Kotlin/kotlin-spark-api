@file:Suppress("UnstableApiUsage")

import com.igormaznitsa.jcp.gradle.JcpTask
import com.vanniktech.maven.publish.JavadocJar.Dokka
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    kotlin
    dokka
    idea
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

    fun implementation(spark: String, scalaCompat: String) {
        val sparkNoPeriods = spark.replace(".", "")
        val scalaCompatNoPeriods = scalaCompat.replace(".", "")
        add(
            "crossBuild${sparkNoPeriods}_${scalaCompatNoPeriods}Implementation",
            "org.apache.spark:spark-sql_${scalaCompatNoPeriods}:$spark"
        )
        add(
            "crossBuild${sparkNoPeriods}_${scalaCompatNoPeriods}Implementation",
            "org.apache.spark:spark-streaming_${scalaCompatNoPeriods}:$spark"
        )
    }

    for (spark in Versions.sparkVersionsForBoth) {
        implementation(spark, "2.12")
        implementation(spark, "2.13")
    }
    for (spark in Versions.sparkVersionsFor2_12) {
        implementation(spark, "2.12")
    }

    with(Dependencies) {
        implementation(
            kotlinStdLib,
            reflect,
//            sparkSql,
//            sparkStreaming,
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

val sparkV: String =
    ((project.extensions.getByName("ext") as ExtraPropertiesExtension).properties["sparkV"] as String?).toString()
val preprocessMain by tasks.creating(JcpTask::class) {
    println("read spark as $sparkV")
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

// TODO
val restoreFolders by tasks.creating {
    doLast {
        idea.module {
            sourceDirs = mutableSetOf(File("./src/main/kotlin"))
            testSourceDirs = mutableSetOf(File("./src/test/kotlin"))
            println("set sources of intellij idea")
        }
    }
}
restoreFolders.dependsOn(tasks.build)



import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate


object Versions {
    const val project = "1.1.1-SNAPSHOT"
    const val groupID = "org.jetbrains.kotlinx.spark"
    const val kotlin = "1.7.0"

    inline val spark get() = System.getProperty("spark") as String
    inline val scala get() = System.getProperty("scala") as String
    inline val sparkMinor get() = spark.substringBeforeLast('.')
    inline val scalaCompat get() = scala.substringBeforeLast('.')

    const val jupyter = "0.11.0-95"
    const val kotest = "5.3.2"
    const val kotestTestContainers = "1.3.3"
    const val dokka = "1.7.0"
    const val jcp = "7.0.5"
    const val mavenPublish = "0.20.0"
    const val atrium = "0.17.0"
    const val kotestExtensionAllure = "1.1.0"
    const val licenseGradlePluginVersion = "0.15.0"
    const val kafkaStreamsTestUtils = "3.1.0"
    const val hadoop = "3.3.1"
    const val kotlinxHtml = "0.7.5"
    const val klaxon = "5.5"

    inline val versionMap
        get() = mapOf(
            "kotlin" to kotlin,
            "scala" to scala,
            "scalaCompat" to scalaCompat,
            "spark" to spark,
            "sparkMinor" to sparkMinor,
        )

    inline val sparkVersionsForBoth get() = listOf("3.3.0", "3.2.1", "3.2.0")
    inline val sparkVersionsFor2_12 get() = listOf("3.1.3", "3.1.2", "3.1.1", "3.1.0", "3.0.3", "3.0.2", "3.0.1", "3.0.0")
}


object Dependencies {
    inline val kotlinStdLib get() = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    inline val reflect get() = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
    inline val scalaLibrary get() = "org.scala-lang:scala-library:${Versions.scala}"
    inline val kotlinxHtml get() = "org.jetbrains.kotlinx:kotlinx-html-jvm:${Versions.kotlinxHtml}"
    inline val sparkSql get() = "org.apache.spark:spark-sql_${Versions.scalaCompat}:${Versions.spark}"
    inline val sparkMl get() = "org.apache.spark:spark-mllib_${Versions.scalaCompat}:${Versions.spark}"
    inline val sparkStreaming get() = "org.apache.spark:spark-streaming_${Versions.scalaCompat}:${Versions.spark}"
    inline val hadoopClient get() = "org.apache.hadoop:hadoop-client:${Versions.hadoop}"
    inline val sparkRepl get() = "org.apache.spark:spark-repl_${Versions.scalaCompat}:${Versions.spark}"
    inline val jupyter get() = "org.jetbrains.kotlinx:kotlin-jupyter-api:${Versions.jupyter}"
    inline val junit get() = "org.junit.jupiter:junit-jupiter-engine:5.8.1"
    inline val sparkStreamingKafka get() = "org.apache.spark:spark-streaming-kafka-0-10_${Versions.scalaCompat}:${Versions.spark}"
    inline val kotest get() = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    inline val kotestTestcontainers get() = "io.kotest.extensions:kotest-extensions-testcontainers:${Versions.kotestTestContainers}"
    inline val klaxon get() = "com.beust:klaxon:${Versions.klaxon}"
    inline val atrium get() = "ch.tutteli.atrium:atrium-fluent-en_GB:${Versions.atrium}"
    inline val kafkaStreamsTestUtils get() = "org.apache.kafka:kafka-streams-test-utils:${Versions.kafkaStreamsTestUtils}"
    inline val jupyterTest get() = "org.jetbrains.kotlinx:kotlin-jupyter-test-kit:${Versions.jupyter}"
    inline val kotlinTest get() = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    inline val kotlinScriptingCommon get() = "org.jetbrains.kotlin:kotlin-scripting-common"
    inline val kotlinScriptingJvm get() = "org.jetbrains.kotlin:kotlin-scripting-jvm"
}





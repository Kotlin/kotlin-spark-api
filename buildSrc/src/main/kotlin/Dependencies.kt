import Versions.hadoop
import Versions.kotestTestContainers
import Versions.kotlin
import Versions.scala
import Versions.scalaCompat
import Versions.spark

object Versions {
    const val project = "1.1.1-SNAPSHOT"
    const val groupID = "org.jetbrains.kotlinx.spark"
    const val kotlin = "1.6.21"

    const val spark = "3.2.1"
    const val scala = "2.12.15"
    const val scalaCompat = "2.12"
    const val jupyter = "0.11.0-95"
    const val kotest = "5.2.3"
    const val kotestTestContainers = "1.3.1"
    const val dokka = "1.6.10"
    const val atrium = "0.17.0"
    const val kotestExtensionAllure = "1.1.0"
    const val licenseGradlePluginVersion = "0.15.0"

    //    const val embeddedKafka = "3.1.0" TODO not used right?
    const val kafkaStreamsTestUtils = "3.1.0"
    const val hadoop = "3.3.1"
    const val kotlinxHtml = "0.7.5"
    const val klaxon = "5.5"
}

object ProvidedDependencies {
    const val sparkSql = "org.apache.spark:spark-sql_$scalaCompat:$spark"
    const val sparkStreaming = "org.apache.spark:spark-streaming_$scalaCompat:$spark"
    const val hadoopClient = "org.apache.hadoop:hadoop-client:$hadoop"
}

object Dependencies {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlin"
    const val scalaLibrary = "org.scala-lang:scala-library:$scala"
    const val kotlinxHtml = "org.jetbrains.kotlinx:kotlinx-html-jvm:${Versions.kotlinxHtml}"

    // Only for Jupyter and Examples
    const val sparkSql = ProvidedDependencies.sparkSql
    const val sparkRepl = "org.apache.spark:spark-repl_$scalaCompat:$spark"
    const val sparkStreaming = ProvidedDependencies.sparkStreaming
    const val hadoopClient = ProvidedDependencies.hadoopClient
    const val jupyter = "org.jetbrains.kotlinx:kotlin-jupyter-api:${Versions.jupyter}"

    // Only for Qodana in Examples
    const val sparkStreamingKafka = TestDependencies.sparkStreamingKafka
}

object RuntimeOnlyTestDependencies {
    const val junit = "org.junit.jupiter:junit-jupiter-engine:5.8.1"
}

object TestDependencies {
    const val sparkStreamingKafka = "org.apache.spark:spark-streaming-kafka-0-10_$scalaCompat:$spark"
    const val kotest = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val kotestTestcontainers = "io.kotest.extensions:kotest-extensions-testcontainers:$kotestTestContainers"

    //    const val embeddedKafka = "" TODO, not used right?
    const val klaxon = "com.beust:klaxon:${Versions.klaxon}"
    const val atrium = "ch.tutteli.atrium:atrium-fluent-en_GB:${Versions.atrium}"
    const val kafkaStreamsTestUtils = "org.apache.kafka:kafka-streams-test-utils:${Versions.kafkaStreamsTestUtils}"
    const val jupyter = "org.jetbrains.kotlinx:kotlin-jupyter-test-kit:${Versions.jupyter}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"

    // Cannot be runtime only for testing
    const val sparkStreaming = ProvidedDependencies.sparkStreaming
}



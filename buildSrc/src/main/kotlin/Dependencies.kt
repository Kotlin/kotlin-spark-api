object Dependencies : Dsl<Dependencies> {
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
    inline val junitJupiterEngine get() = "org.junit.jupiter:junit-jupiter-engine:${Versions.junitJupiterEngine}"
    // must be platform()
    inline val junitBom get() = "org.junit:junit-bom:${Versions.junitJupiterEngine}"
    inline val junitJupiter get() = "org.junit.jupiter:junit-jupiter"
    inline val junitPlatformCommons get() = "org.junit.platform:junit-platform-commons"
    inline val junitPlatformLauncher get() = "org.junit.platform:junit-platform-launcher"
    inline val junitPlatformRunner get() = "org.junit.platform:junit-platform-runner"
    inline val junitPlatformSuiteApi get() = "org.junit.platform:junit-platform-suite-api"

    inline val junit get() = "junit:junit:${Versions.junit}"
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
    inline val jacksonDatabind get() = "com.fasterxml.jackson.core:jackson-databind:${Versions.jacksonDatabind}"
    inline val kotlinDateTime get() = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinxDateTime}"
    inline val kotlinCompiler get() = "org.jetbrains.kotlin:kotlin-compiler:${Versions.kotlin}"
    inline val kotlinScriptRuntime get() = "org.jetbrains.kotlin:kotlin-script-runtime:${Versions.kotlin}"
    inline val kotlinAnnotationsJvm get() = "org.jetbrains.kotlin:kotlin-annotations-jvm:${Versions.kotlin}"
    inline val kotlinCompilerInternalTestFramework get() = "org.jetbrains.kotlin:kotlin-compiler-internal-test-framework:${Versions.kotlin}"
    inline val kotlinGradlePlugin get() = "org.jetbrains.kotlin:kotlin-gradle-plugin"
}





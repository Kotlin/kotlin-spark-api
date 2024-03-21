object Versions {
    const val project = "1.2.5-SNAPSHOT"
    const val groupID = "org.jetbrains.kotlinx.spark"
    const val kotlin = "1.9.22"
    const val jvmTarget = "8"
    const val jupyterJvmTarget = "8"
    inline val spark get() = System.getProperty("spark") as String

    inline val scala get() = System.getProperty("scala") as String
    inline val sparkMinor get() = spark.substringBeforeLast('.')
    inline val scalaCompat get() = scala.substringBeforeLast('.')

    // TODO
    inline val sparkConnect get() = System.getProperty("sparkConnect", "false").toBoolean()

    const val jupyter = "0.12.0-32-1"

    const val kotest = "5.5.4"
    const val kotestTestContainers = "1.3.3"
    const val dokka = "1.8.20"
    const val jcp = "7.0.5"
    const val mavenPublish = "0.20.0"
    const val atrium = "0.17.0"
    const val licenseGradlePluginVersion = "0.15.0"
    const val kafkaStreamsTestUtils = "3.1.0"
    const val hadoop = "3.3.6"
    const val kotlinxHtml = "0.7.5"
    const val klaxon = "5.5"
    const val jacksonDatabind = "2.13.4.2"
    const val kotlinxDateTime = "0.6.0-RC.2"

    inline val versionMap: Map<String, String>
        get() = mapOf(
            "kotlin" to kotlin,
            "scala" to scala,
            "scalaCompat" to scalaCompat,
            "spark" to spark,
            "sparkMinor" to sparkMinor,
            "version" to project,
            "sparkConnect" to sparkConnect.toString(),
        )
}

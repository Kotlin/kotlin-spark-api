object Versions {
    const val project = "1.2.0"
    const val groupID = "org.jetbrains.kotlinx.spark"
    const val kotlin = "1.7.10"
    const val jvmTarget = "11"

    inline val spark get() = System.getProperty("spark") as String
    inline val scala get() = System.getProperty("scala") as String
    inline val sparkMinor get() = spark.substringBeforeLast('.')
    inline val scalaCompat get() = scala.substringBeforeLast('.')

    const val jupyter = "0.11.0-134"
    const val kotest = "5.3.2"
    const val kotestTestContainers = "1.3.3"
    const val dokka = "1.7.10"
    const val jcp = "7.0.5"
    const val mavenPublish = "0.20.0"
    const val atrium = "0.17.0"
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
            "version" to project,
        )

}

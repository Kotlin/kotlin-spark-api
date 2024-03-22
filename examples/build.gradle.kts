import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Needs to be installed in the local maven repository
    id("org.jetbrains.kotlinx.spark.api")
    kotlin
}

//kotlinSparkApi {
//    enabled = true
//    sparkifyAnnotationFqNames = listOf(
//        "org.jetbrains.kotlinx.spark.api.plugin.annotations.Sparkify",
//    )
//}

group = Versions.groupID
version = Versions.project

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    Projects {
        implementation(
            kotlinSparkApi,
        )
    }

    Dependencies {

        // https://github.com/FasterXML/jackson-bom/issues/52
        if (Versions.spark == "3.3.1") implementation(jacksonDatabind)

        implementation(
            sparkSql,
            sparkMl,
            sparkStreaming,
            sparkStreamingKafka,
        )
    }
}

kotlin {
    jvmToolchain(8)
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(Versions.jvmTarget)
    }
}
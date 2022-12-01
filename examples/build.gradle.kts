import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin
    idea
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

dependencies {

    with(Projects) {
        implementation(
            kotlinSparkApi,
        )
    }

    with(Dependencies) {
        implementation(
            sparkSql,
            jacksonDatabind, // Spark 3.3.1 https://github.com/FasterXML/jackson-bom/issues/52
            sparkMl,
            sparkStreaming,
            sparkStreamingKafka,
        )

    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(
            JavaLanguageVersion.of(Versions.jvmTarget)
        )
    }
}

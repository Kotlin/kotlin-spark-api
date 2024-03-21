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
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(Versions.jvmTarget)
    }
}

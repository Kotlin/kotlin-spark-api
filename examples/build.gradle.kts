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
            sparkMl,
            sparkStreaming,
            sparkStreamingKafka,
        )

    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }
}

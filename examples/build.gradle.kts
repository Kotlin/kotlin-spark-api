plugins {
    kotlin
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

dependencies {
    implementation(
        project(":kotlin-spark-api"),
    )

    with(Dependencies) {
        implementation(
            sparkSql,
            sparkStreaming,
            sparkStreamingKafka,
        )
    }
}

plugins {
    kotlin
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

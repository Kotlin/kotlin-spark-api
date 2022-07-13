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

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }
}
tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }
}
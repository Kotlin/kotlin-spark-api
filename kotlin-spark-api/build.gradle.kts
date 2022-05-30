plugins {
    scala
    kotlin("jvm")
    id("org.jetbrains.dokka") version Versions.dokka
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {

    api(
        project(":core"),
        project(":scala-tuples-in-kotlin"),
    )

    with(Dependencies) {
        implementation(
            kotlinStdLib,
            reflect,
        )
    }

    with(RuntimeOnlyDependencies) {
        runtimeOnly(
            sparkSql,
            sparkStreaming,
            hadoopClient,
        )
    }

    with(TestDependencies) {
        testImplementation(
            sparkStreamingKafka,
            kotest,
            kotestTestcontainers,
            klaxon,
            atrium,
            sparkStreaming,
            kafkaStreamsTestUtils,
        )
    }
}


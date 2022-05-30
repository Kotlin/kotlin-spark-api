plugins {
    scala
    kotlin
    dokka
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
    with(Dependencies) {
        implementation(
            kotlinStdLib,
            scalaLibrary,
        )
    }

    with(TestDependencies) {
        testImplementation(
            kotest,
            klaxon,
            atrium,
            kotlinTest,
        )
    }
}

plugins {
    scala
    kotlin
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

dependencies {

    with(Dependencies) {
        api(
            scalaLibrary,
            reflect,
        )
    }

    with(ProvidedDependencies) {
        implementation(
            sparkSql,
        )
    }
}
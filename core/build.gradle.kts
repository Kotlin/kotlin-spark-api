plugins {
    scala
    kotlin("jvm")
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

    with(RuntimeOnlyDependencies) {
        runtimeOnly(sparkSql)
    }
}
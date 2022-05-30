plugins {
    kotlin("jvm")
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":kotlin-spark-api"))
}

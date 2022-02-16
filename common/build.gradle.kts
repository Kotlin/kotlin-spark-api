import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    kotlin("jvm")
}

val groupID: String by project
val projectVersion: String by project

group = groupID
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
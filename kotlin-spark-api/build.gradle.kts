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
    val scalaVersion: String by project
    val scalaCompatVersion: String by project
    val spark3Version: String by project

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.scala-lang:scala-library:$scalaVersion")
    implementation("org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version")
    implementation(kotlin("reflect"))

    implementation(project(":core"))
    implementation(project(":common"))

}
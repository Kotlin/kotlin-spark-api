plugins {
    scala
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

    api("org.scala-lang:scala-library:$scalaVersion")
    api("org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version")
    api(kotlin("reflect"))


}
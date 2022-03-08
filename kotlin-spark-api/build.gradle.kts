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
    val kotestVersion: String by project
    val kotestExtensionAllureVersion: String by project
    val klaxonVersion: String by project
    val atriumVersion: String by project

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.scala-lang:scala-library:$scalaVersion")
    implementation("org.apache.spark:spark-sql_$scalaCompatVersion:$spark3Version")
    implementation(kotlin("reflect"))

    implementation(project(":core"))


    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-allure:$kotestExtensionAllureVersion")
    testImplementation("com.beust:klaxon:$klaxonVersion")
    testImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:$atriumVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
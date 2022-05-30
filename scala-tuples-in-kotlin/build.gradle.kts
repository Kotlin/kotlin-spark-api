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
    val kotestVersion: String by project
    val kotestExtensionAllureVersion: String by project
    val klaxonVersion: String by project
    val atriumVersion: String by project

    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-allure:$kotestExtensionAllureVersion")
    testImplementation("com.beust:klaxon:$klaxonVersion")
    testImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:$atriumVersion")
}

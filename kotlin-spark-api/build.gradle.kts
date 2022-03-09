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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    val kotestVersion: String by project
    val kotestExtensionAllureVersion: String by project
    val klaxonVersion: String by project
    val atriumVersion: String by project

    implementation(kotlin("stdlib-jdk8"))

    api(project(":core"))

//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
//    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
//    testImplementation("io.kotest:kotest-framework-engine-jvm:$kotestVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-allure:$kotestExtensionAllureVersion")
    testImplementation("com.beust:klaxon:$klaxonVersion")
    testImplementation("ch.tutteli.atrium:atrium-fluent-en_GB:$atriumVersion")
}

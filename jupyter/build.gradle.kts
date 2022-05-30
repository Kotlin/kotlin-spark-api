plugins {
    scala
    kotlin
    dokka
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {

    implementation(// todo or api(
        project(":kotlin-spark-api"),
    )

    with(Dependencies) {
        implementation(
            kotlinStdLib,
            kotlinxHtml,
            sparkSql,
            sparkRepl,
            sparkStreaming,
            hadoopClient,
            jupyter,
        )
    }

    with(TestDependencies) {
        testImplementation(
            kotest,
            jupyter,
        )
    }
}

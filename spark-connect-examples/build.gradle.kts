import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Needs to be installed in the local maven repository or have the bootstrap jar on the classpath
    id("org.jetbrains.kotlinx.spark.api")
    kotlin("jvm")
    application
}

// run with `./gradlew run`
application {
    mainClass = "org.jetbrains.kotlinx.spark.examples.MainKt"

    // workaround for java 17
    applicationDefaultJvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}

kotlinSparkApi {
    enabled = true
    sparkifyAnnotationFqNames = listOf("org.jetbrains.kotlinx.spark.api.plugin.annotations.Sparkify")
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    Projects {
        implementation(
            // TODO kotlinSparkApi,
        )
    }

    Dependencies {

        // IMPORTANT!
        compileOnly(sparkSqlApi)
        implementation(sparkConnectClient)
    }
}

// spark-connect seems to work well with java 17 as client and java 1.8 as server
// also set gradle and your project sdk to java 17
kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

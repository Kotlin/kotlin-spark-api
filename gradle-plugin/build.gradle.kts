@file:Suppress("UnstableApiUsage")

plugins {
    `java-gradle-plugin`
    kotlin
    buildconfig
    signing
    gradlePublishPlugin
}

group = Versions.groupID
version = Versions.project

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("~/.m2/repository")
        }
    }
}

gradlePlugin {
    website = "https://github.com/Kotlin/kotlin-spark-api"
    vcsUrl = "https://github.com/Kotlin/kotlin-spark-api"

    plugins.create("kotlin-spark-api") {
        id = "${Versions.groupID}.api"
        displayName = "Kotlin Spark API (Gradle) Compiler Plugin"
        description = "TODO"
        tags = setOf("kotlin", "spark", "compiler", "gradle", "Sparkify", "columnName")
        implementationClass = "${Versions.groupID}.api.gradlePlugin.SparkKotlinCompilerGradlePlugin"
    }
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

dependencies {
    Dependencies {
        compileOnly(
            kotlinStdLib,
            kotlinGradlePlugin,
            gradleApi(),
            gradleKotlinDsl()
        )
    }
}

//tasks.withType<ShadowJar> {
//    isZip64 = true
//    archiveClassifier = ""
//}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(Versions.jvmTarget)
    }
}
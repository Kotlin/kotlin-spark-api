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

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(Versions.jvmTarget)
    }
}

/**
 * Copies the built jar file to the gradle/bootstraps directory.
 * This allows the project to use the gradle plugin without mavenLocal.
 */
val updateBootstrapVersion by tasks.creating(Copy::class) {
    group = "build"
    dependsOn(tasks.jar)

    val jarFile = tasks.jar.get().outputs.files.files.single {
        it.extension == "jar" && it.name.startsWith("gradle-plugin")
    }
    from(jarFile)
    rename { "gradle-plugin.jar" }
    into(project.rootDir.resolve("gradle/bootstraps"))
    outputs.upToDateWhen { false }
}

tasks.build {
    finalizedBy(updateBootstrapVersion)
}

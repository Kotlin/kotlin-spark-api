@file:Suppress("UnstableApiUsage")

import Projects.compilerPlugin
import Projects.gradlePlugin
import com.github.gmazzo.buildconfig.BuildConfigExtension


buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(jcp)
        classpath(mavenPublish)
    }
}

plugins {
    mavenPublish version Versions.mavenPublish
    dokka version Versions.dokka
    idea
    kotlin version Versions.kotlin apply false
    buildconfig version Versions.buildconfig apply false

    // Needs to be installed in the local maven repository
    kotlinSparkApi version Versions.kotlinSparkApiGradlePlugin apply false
}

group = Versions.groupID
version = Versions.project

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

allprojects {
    plugins.withId(mavenPublishBase) {
        group = Versions.groupID
        version = Versions.project

        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/Kotlin/kotlin-spark-api")
                    credentials {
                        username = project.findProperty("gpr.user") as String?
                            ?: System.getenv("GITHUB_ACTOR")
                        password = project.findProperty("gpr.key") as String?
                            ?: System.getenv("GITHUB_TOKEN")
                    }
                }
            }
        }

        mavenPublishing {
            pomFromGradleProperties()
            publishToMavenCentral()
            // The username and password for Sonatype OSS can be provided as Gradle properties
            // called mavenCentralUsername and mavenCentralPassword to avoid having to commit them.
            // You can also supply them as environment variables called
            // ORG_GRADLE_PROJECT_mavenCentralUsername and
            // ORG_GRADLE_PROJECT_mavenCentralPassword.

            // also ORG_GRADLE_PROJECT_signingInMemoryKey=exported_ascii_armored_key
            // # optional
            // ORG_GRADLE_PROJECT_signingInMemoryKeyId=24875D73
            // # if key was created with a password
            // ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=secret

            signAllPublications()
            pom {
                name.set("Kotlin Spark API")
                description.set("Kotlin for Apache Spark")
                packaging = "pom"

                url.set("https://maven.apache.org")
                inceptionYear.set("2019")

                organization {
                    name.set("JetBrains")
                    url.set("https://www.jetbrains.com/")
                }

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("asm0dey")
                        name.set("Pasha Finkelshteyn")
                        email.set("asm0dey@jetbrains.com")
                        timezone.set("GMT+3")
                    }
                    developer {
                        id.set("vitaly.khudobakhshov")
                        name.set("Vitaly Khudobakhshov")
                        email.set("vitaly.khudobakhshov@jetbrains.com")
                        timezone.set("GMT+3")
                    }
                    developer {
                        id.set("Jolanrensen")
                        name.set("Jolan Rensen")
                        email.set("jolan.rensen@jetbrains.com")
                        timezone.set("GMT+1")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/Kotlin/kotlin-spark-api.git")
                    url.set("https://github.com/Kotlin/kotlin-spark-api")
                    tag.set("HEAD")
                }
            }
        }
    }
}

subprojects {
    afterEvaluate {
        extensions.findByType<BuildConfigExtension>()?.apply {
            val projectVersion = Versions.project
            val groupId = Versions.groupID

            val compilerPluginArtifactId = compilerPlugin.name
            val gradlePluginArtifactId = gradlePlugin.name

            val compilerPluginId = "$groupId.api"

            val defaultSparkifyFqName = "$groupId.api.plugin.annotations.Sparkify"
            val defaultColumnNameFqName = "$groupId.api.plugin.annotations.ColumnName"

            val projectRoot = project.rootDir.absolutePath

            packageName("$groupId.api")
            className("Artifacts")

            buildConfigField("compilerPluginId", compilerPluginId)
            buildConfigField("groupId", groupId)
            buildConfigField("gradlePluginArtifactId", gradlePluginArtifactId)
            buildConfigField("projectVersion", projectVersion)
            buildConfigField("compilerPluginArtifactId", compilerPluginArtifactId)

            buildConfigField("defaultSparkifyFqName", defaultSparkifyFqName)
            buildConfigField("defaultColumnNameFqName", defaultColumnNameFqName)
            buildConfigField("projectRoot", projectRoot)

            buildConfigField("scalaVersion", Versions.scala)
            buildConfigField("sparkVersion", Versions.spark)
        }
    }
}
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
}

plugins {
    id("com.gradle.enterprise") version "3.10.3"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

val spark: String by settings
val scala: String by settings
val skipScalaOnlyDependent: String by settings
System.setProperty("spark", spark)
System.setProperty("scala", scala)
System.setProperty("skipScalaOnlyDependent", skipScalaOnlyDependent)

val scalaCompat
    get() = scala.substringBeforeLast('.')

val versions = "${spark}_${scalaCompat}"

rootProject.name = "kotlin-spark-api-parent_$versions"

include("scala-helpers")
include("scala-tuples-in-kotlin")
include("kotlin-spark-api")
include("jupyter")
include("examples")
include("compiler-plugin")
include("gradle-plugin")

// just scala dependent
project(":scala-helpers").name = "scala-helpers_$scalaCompat"
project(":scala-tuples-in-kotlin").name = "scala-tuples-in-kotlin_$scalaCompat"

// spark+scala dependent
project(":kotlin-spark-api").name = "kotlin-spark-api_$versions"
project(":jupyter").name = "jupyter_$versions"
project(":examples").name = "examples_$versions"

buildCache {
    local {
        removeUnusedEntriesAfterDays = 30
    }
}

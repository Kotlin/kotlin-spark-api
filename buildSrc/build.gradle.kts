import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}
tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}
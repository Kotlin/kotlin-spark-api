import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin
    mavenPublish
    buildconfig
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

sourceSets {
    test {
        val srcDirs = listOf("src/test-gen/kotlin")
        kotlin.srcDirs(srcDirs)
        java.srcDirs(srcDirs)
    }
}

dependencies {
    Dependencies {
        compileOnly(kotlinCompiler)

        testRuntimeOnly(
            kotlinTest,
            kotlinScriptRuntime,
            kotlinAnnotationsJvm,
        )

        testImplementation(
            kotlinCompiler,
            reflect,
            kotlinCompilerInternalTestFramework,
            junit,

            platform(junitBom),
            junitJupiter,
            junitPlatformCommons,
            junitPlatformLauncher,
            junitPlatformRunner,
            junitPlatformSuiteApi,
        )
    }
}

tasks.test {
    useJUnitPlatform()
    doFirst {
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
            "-Xcontext-receivers"
        )
        languageVersion = KotlinVersion.KOTLIN_2_0
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

val generateTests by tasks.creating(JavaExec::class) {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("org.jetbrains.kotlinx.spark.api.compilerPlugin.GenerateTestsKt")
}

val compileTestKotlin by tasks.getting {
    doLast {
        generateTests.exec()
    }
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path = project.configurations
        .testRuntimeClasspath.get()
        .files
        .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
        ?.absolutePath
        ?: return
    systemProperty(propName, path)
}
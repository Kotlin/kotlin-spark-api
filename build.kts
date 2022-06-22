#!/usr/bin/env kotlin

import java.io.File

data class Version(val spark: String, val scala: String) {

    val scalaCompat
        get() = scala
            .split(".")
            .take(2)
            .joinToString(".")

    val sparkMinor
        get() = spark
            .split(".")
            .take(2)
            .joinToString(".")
}

val scala2_13Version = "2.13.8"
val scala2_12Version = "2.12.15"

val versions = buildList {
    listOf(
        scala2_13Version,
        scala2_12Version,
    ).forEach { scala ->
        listOf(
            "3.3.0",

            "3.2.1",
            "3.2.0",
        ).forEach { spark ->
            this += Version(spark, scala)
        }
    }

    listOf(
        "3.1.3",
        "3.1.2",
        "3.1.1",
        "3.1.0",

        "3.0.3",
        "3.0.2",
        "3.0.1",
        "3.0.0",
    ).forEach { spark ->
        this += Version(spark, scala2_12Version)
    }
}
println("building ${versions.size} versions of the API")


val skipFiles = listOf("build.kts", ".github", ".idea", ".gitignore", "generated_")
val files = File(".").listFiles()!!.toList()
    .filterNot { file -> skipFiles.any { it in file.name } }


val cleanFolders = listOf("target", "build", "generated-target")
for (version in versions) {
    with(version) {
        val folder = File("./generated_${scalaCompat}_$spark")
        if (folder.exists()) {
            folder.deleteRecursively()
        }
        folder.mkdir()
        files.forEach {
            it.copyRecursively(File(folder.path, it.name))
        }

        for (file in folder.walk()) {
            if (file.name in cleanFolders) {
                file.deleteRecursively()
                continue
            }

            if ("pom" !in file.name || file.extension != "xml") continue

            var text = file.readText()
            text = text.replace(
                oldValue = "_scalaAndSparkVersion",
                newValue = "_${scalaCompat}_$spark",
            )

            text = text.replace(
                oldValue = "(Scala TODO, Spark TODO)",
                newValue = "(Scala $scalaCompat, Spark $spark)"
            )

            val regex = Regex(
                """(<!-- \${'$'}Autogenerate start\${'$'} -->)\p{all}*<!-- \${'$'}Autogenerate end\${'$'} -->"""
            )
            if (text.matches(regex)) text.replace(
                regex = regex,
                replacement = """
                <!-- ${'$'}Autogenerate start${'$'} -->
                <scala.version>$scala</scala.version>
                <scala.compat.version>$scalaCompat</scala.compat.version>
                <spark3.version>$spark</spark3.version>
                <spark3.minor.version>$sparkMinor</spark3.minor.version>
                <!-- ${'$'}Autogenerate end${'$'} -->
                """.trimIndent(),
            )
            file.writeText(text)
            println("edited ${file.path}")
        }

        // TODO
//        Runtime.getRuntime().exec("chmod +X ${folder.absolutePath}/mvnw")
//        Runtime.getRuntime().exec("${folder.absolutePath}/mvnw test")
    }
}

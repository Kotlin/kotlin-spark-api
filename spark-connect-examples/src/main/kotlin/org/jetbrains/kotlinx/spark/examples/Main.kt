package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.connect.client.REPLClassDirMonitor

// run with `./gradlew run` or set VM options: "--add-opens=java.base/java.nio=ALL-UNNAMED" in the IDE
fun main() {
    val spark =
        SparkSession
            .builder()
            .remote("sc://localhost")
            .create()

    val classFinder = REPLClassDirMonitor("/mnt/data/Projects/kotlin-spark-api/spark-connect-examples/build/classes")
    spark.registerClassFinder(classFinder)
    spark.addArtifact("/mnt/data/Projects/kotlin-spark-api/spark-connect-examples/build/libs/spark-connect-examples-2.0.0-SNAPSHOT.jar")

    spark.sql("select 1").show()

    spark.stop()
}

//@Sparkify
//data class Person(
//    val name: String,
//    val age: Int,
//)

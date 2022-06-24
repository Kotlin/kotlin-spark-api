import org.gradle.api.artifacts.dsl.DependencyHandler

object Projects {

    val DependencyHandler.kotlinSparkApi
        get() = project(":kotlin-spark-api")

    val DependencyHandler.core
        get() = project(":core")

    val DependencyHandler.examples
        get() = project(":examples")

    val DependencyHandler.jupyter
        get() = project(":jupyter")

    val DependencyHandler.scalaTuplesInKotlin
        get() = project(":scala-tuples-in-kotlin")
}
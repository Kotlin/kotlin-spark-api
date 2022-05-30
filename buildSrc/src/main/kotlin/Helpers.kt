import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.api(vararg dependencyNotations: Any): List<Dependency?> =
    dependencyNotations.map {
        add("api", it)
    }

fun DependencyHandler.testImplementation(vararg dependencyNotations: Any): List<Dependency?> =
    dependencyNotations.map {
        add("testImplementation", it)
    }

fun DependencyHandler.implementation(vararg dependencyNotations: Any): List<Dependency?> =
    dependencyNotations.map {
        add("implementation", it)
    }

fun DependencyHandler.runtimeOnly(vararg dependencyNotations: Any): List<Dependency?> =
    dependencyNotations.map {
        add("runtimeOnly", it)
    }
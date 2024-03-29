import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.testApi(vararg dependencyNotations: Any): List<Dependency?> =
    dependencyNotations.map {
        add("testApi", it)
    }

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

fun DependencyHandler.project(
    path: String,
    configuration: String? = null
): ProjectDependency = project(
    if (configuration != null) mapOf("path" to path, "configuration" to configuration)
    else mapOf("path" to path)
) as ProjectDependency

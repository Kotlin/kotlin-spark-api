package org.jetbrains.kotlinx.spark.api.compilerPlugin

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.kotlinx.spark.api.Artifacts
import org.jetbrains.kotlinx.spark.api.compilerPlugin.runners.AbstractBoxTest
import org.jetbrains.kotlinx.spark.api.compilerPlugin.runners.AbstractDiagnosticTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = "${Artifacts.projectRoot}/${Artifacts.compilerPluginArtifactId}/src/test/resources/testData",
            testsRoot = "${Artifacts.projectRoot}/${Artifacts.compilerPluginArtifactId}/src/test-gen/kotlin",
        ) {
//            testClass<AbstractDiagnosticTest> {
//                model("diagnostics")
//            }

            testClass<AbstractBoxTest> {
                model("box")
            }
        }
    }
}



package org.jetbrains.kotlinx.spark.api.compilerPlugin.runners;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlinx.spark.api.compilerPlugin.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("/mnt/data/Projects/kotlin-spark-api/compiler-plugin/src/test/resources/testData/diagnostics")
@TestDataPath("$PROJECT_ROOT")
public class DiagnosticTestGenerated extends AbstractDiagnosticTest {
  @Test
  public void testAllFilesPresentInDiagnostics() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("/mnt/data/Projects/kotlin-spark-api/compiler-plugin/src/test/resources/testData/diagnostics"), Pattern.compile("^(.+)\\.kt$"), null, true);
  }

  @Test
  @TestMetadata("dataClassTest.kt")
  public void testDataClassTest() {
    runTest("/mnt/data/Projects/kotlin-spark-api/compiler-plugin/src/test/resources/testData/diagnostics/dataClassTest.kt");
  }
}

# Kotlin for Apache® Spark™ [![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.kotlinx.spark/kotlin-spark-api-parent.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:org.jetbrains.kotlinx.spark%20AND%20v:1.0.0-preview1) [![official JetBrains project](http://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)


Your next API to work with  [Apache Spark](https://spark.apache.org/). 

This project adds a missing layer of compatibility between [Kotlin](https://kotlinlang.org/) and [Apache Spark](https://spark.apache.org/).
It allows Kotlin developers to use familiar language features such as data classes, and lambda expressions as simple expressions in curly braces or method references. 

We have opened a Spark Project Improvement Proposal: [Kotlin support for Apache Spark](http://issues.apache.org/jira/browse/SPARK-32530#) to work with the community towards getting Kotlin support as a first-class citizen in Apache Spark. We encourage you to voice your opinions and participate in the discussion.

## Table of Contents

- [Supported versions of Apache Spark](#supported-versions-of-apache-spark)
- [Releases](#releases)
- [How to configure Kotlin for Apache Spark in your project](#how-to-configure-kotlin-for-apache-spark-in-your-project)
- [Kotlin for Apache Spark features](#kotlin-for-apache-spark-features)
    - [Creating a SparkSession in Kotlin](#creating-a-sparksession-in-kotlin)
    - [Creating a Dataset in Kotlin](#creating-a-dataset-in-kotlin)
    - [Null safety](#null-safety)
    - [withSpark function](#withspark-function)
    - [withCached function](#withcached-function)
    - [toList and toArray](#tolist-and-toarray-methods)
- [Examples](#examples)
- [Reporting issues/Support](#reporting-issuessupport)
- [Code of Conduct](#code-of-conduct)
- [License](#license)

## Supported versions of Apache Spark

| Apache Spark | Scala |  Kotlin for Apache Spark |
|:------------:|:-----------:|:------------:|
| 3.0.0        | 2.12 | kotlin-spark-api-3.0.0_2.12:1.0.0-preview1    |

## Releases

The list of Kotlin for Apache Spark releases is available [here](https://github.com/JetBrains/kotlin-spark-api/releases/).
The Kotlin for Spark artifacts adhere to the following convention:
`[Apache Spark version]_[Scala core version]:[Kotlin for Apache Spark API version]` 

[![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.kotlinx.spark/kotlin-spark-api-parent.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.jetbrains.kotlinx.spark%22%20AND%20a:%22kotlin-spark-api-3.0.0_2.12%22)

## How to configure Kotlin for Apache Spark in your project

You can add Kotlin for Apache Spark as a dependency to your project: `Maven`, `Gradle`, `SBT`, and `leinengen` are supported.
 
Here's an example `pom.xml`:

```xml
<dependency>
  <groupId>org.jetbrains.kotlinx.spark</groupId>
  <artifactId>kotlin-spark-api-3.0.0_2.12</artifactId>
  <version>${kotlin-spark-api.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.12</artifactId>
    <version>${spark.version}</version>
</dependency>
```

Note that `core` is being compiled against Scala version `2.12`.  
You can find a complete example with `pom.xml` and `build.gradle` in the [Quick Start Guide](https://github.com/JetBrains/kotlin-spark-api/wiki/Quick-Start-Guide).

Once you have configured the dependency, you only need to add the following import to your Kotlin file: 
```kotlin
import org.jetbrains.kotlinx.spark.api.*
```   

## Kotlin for Apache Spark features

### Creating a SparkSession in Kotlin
```kotlin
val spark = SparkSession
        .builder()
        .master("local[2]")
        .appName("Simple Application").orCreate

```

### Creating a Dataset in Kotlin
```kotlin
spark.toDS("a" to 1, "b" to 2)
```
The example above produces `Dataset<Pair<String, Int>>`.
 
### Null safety
There are several aliases in API, like `leftJoin`, `rightJoin` etc. These are null-safe by design. 
For example, `leftJoin` is aware of nullability and returns `Dataset<Pair<LEFT, RIGHT?>>`.
Note that we are forcing `RIGHT` to be nullable for you as a developer to be able to handle this situation. 
`NullPointerException`s are hard to debug in Spark, and we doing our best to make them as rare as possible.

### withSpark function

We provide you with useful function `withSpark`, which accepts everything that may be needed to run Spark — properties, name, master location and so on. It also accepts a block of code to execute inside Spark context.

After work block ends, `spark.stop()` is called automatically.

```kotlin
withSpark {
    dsOf(1, 2)
            .map { it to it }
            .show()
}
```

`dsOf` is just one more way to create `Dataset` (`Dataset<Int>`) from varargs.

### withCached function
It can easily happen that we need to fork our computation to several paths. To compute things only once we should call `cache`
method. However, it becomes difficult to control when we're using cached `Dataset` and when not.
It is also easy to forget to unpersist cached data, which can break things unexpectedly or take up more memory
than intended.

To solve these problems we've added `withCached` function

```kotlin
withSpark {
    dsOf(1, 2, 3, 4, 5)
            .map { it to (it + 2) }
            .withCached {
                showDS()

                filter { it.first % 2 == 0 }.showDS()
            }
            .map { c(it.first, it.second, (it.first + it.second) * 2) }
            .show()
}
```

Here we're showing cached `Dataset` for debugging purposes then filtering it. 
The `filter` method returns filtered `Dataset` and then the cached `Dataset` is being unpersisted, so we have more memory t
o call the `map` method and collect the resulting `Dataset`.

### toList and toArray methods

For more idiomatic Kotlin code we've added `toList` and `toArray` methods in this API. You can still use the `collect` method as in Scala API, however the result should be casted to `Array`.
  This is because `collect` returns a Scala array, which is not the same as Java/Kotlin one.

## Examples

For more, check out [examples](https://github.com/JetBrains/kotlin-spark-api/tree/master/examples/src/main/kotlin/org/jetbrains/kotlinx/spark/examples) module.
To get up and running quickly, check out this [tutorial](https://github.com/JetBrains/kotlin-spark-api/wiki/Quick-Start-Guide). 

## Reporting issues/Support
Please use [GitHub issues](https://github.com/JetBrains/kotlin-spark-api/issues) for filing feature requests and bug reports.
You are also welcome to join [kotlin-spark channel](https://kotlinlang.slack.com/archives/C015B9ZRGJF) in the Kotlin Slack.

## Code of Conduct
This project and the corresponding community is governed by the [JetBrains Open Source and Community Code of Conduct](https://confluence.jetbrains.com/display/ALL/JetBrains+Open+Source+and+Community+Code+of+Conduct). Please make sure you read it. 

## License
Kotlin for Apache Spark is licensed under the [Apache 2.0 License](LICENSE).



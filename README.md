# Kotlin for Apache® Spark™ [![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.kotlinx.spark/kotlin-spark-api-parent.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:org.jetbrains.kotlinx.spark%20AND%20v:1.1.0) [![official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub) [![Join the chat at https://gitter.im/JetBrains/kotlin-spark-api](https://badges.gitter.im/JetBrains/kotlin-spark-api.svg)](https://gitter.im/JetBrains/kotlin-spark-api?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


Your next API to work with  [Apache Spark](https://spark.apache.org/). 

This project adds a missing layer of compatibility between [Kotlin](https://kotlinlang.org/) and [Apache Spark](https://spark.apache.org/).
It allows Kotlin developers to use familiar language features such as data classes, and lambda expressions as simple expressions in curly braces or method references. 

We have opened a Spark Project Improvement Proposal: [Kotlin support for Apache Spark](http://issues.apache.org/jira/browse/SPARK-32530) to work with the community towards getting Kotlin support as a first-class citizen in Apache Spark. We encourage you to voice your opinions and participate in the discussion.

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
    - [Column infix/operator functions](#column-infixoperator-functions)
    - [Overload Resolution Ambiguity](#overload-resolution-ambiguity)
    - [Tuples](#tuples)
    - [Streaming](#streaming)
- [Examples](#examples)
- [Reporting issues/Support](#reporting-issuessupport)
- [Code of Conduct](#code-of-conduct)
- [License](#license)

## Supported versions of Apache Spark

| Apache Spark | Scala |     Kotlin for Apache Spark     |
|:------------:|:-----:|:-------------------------------:|
|    3.2.1+    | 2.12  |   kotlin-spark-api-3.2:1.1.0    |
|    3.1.3+    | 2.12  |   kotlin-spark-api-3.1:1.1.0    |
|    3.0.3+    | 2.12  |   kotlin-spark-api-3.0:1.1.0    |
|    2.4.1+    | 2.12  | kotlin-spark-api-2.4_2.12:1.0.2 |
|    2.4.1+    | 2.11  | kotlin-spark-api-2.4_2.11:1.0.2 |

## Releases

The list of Kotlin for Apache Spark releases is available [here](https://github.com/JetBrains/kotlin-spark-api/releases/).
The Kotlin for Spark artifacts adhere to the following convention:
`[Apache Spark version]_[Scala core version]:[Kotlin for Apache Spark API version]` 

[![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.kotlinx.spark/kotlin-spark-api-parent.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:"org.jetbrains.kotlinx.spark"%20AND%20a:"kotlin-spark-api-3.2")

## How to configure Kotlin for Apache Spark in your project

You can add Kotlin for Apache Spark as a dependency to your project: `Maven`, `Gradle`, `SBT`, and `leinengen` are supported.
 
Here's an example `pom.xml`:

```xml
<dependency>
  <groupId>org.jetbrains.kotlinx.spark</groupId>
  <artifactId>kotlin-spark-api-3.2</artifactId>
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

### Jupyter

The Kotlin Spark API also supports Kotlin Jupyter notebooks.
To it, simply add

```jupyterpython
%use spark
```
to the top of your notebook. This will get the latest version of the API, together with the latest version of Spark.
To define a certain version of Spark or the API itself, simply add it like this:
```jupyterpython
%use spark(spark=3.2, v=1.1.0)
```

Inside the notebook a Spark session will be initiated automatically. This can be accessed via the `spark` value.
`sc: JavaSparkContext` can also be accessed directly. The API operates pretty similarly.

There is also support for HTML rendering of Datasets and simple (Java)RDDs.
Check out the [example](examples/src/main/kotlin/org/jetbrains/kotlinx/spark/examples/JupyterExample.ipynb) as well.


To use Spark Streaming abilities, instead use
```jupyterpython
%use spark-streaming
```
This does not start a Spark session right away, meaning you can call `withSparkStreaming(batchDuration) {}` 
in whichever cell you want.
Check out the [example](examples/src/main/kotlin/org/jetbrains/kotlinx/spark/examples/streaming/JupyterStreamingExample.ipynb).

## Kotlin for Apache Spark features

### Creating a SparkSession in Kotlin
```kotlin
val spark = SparkSession
        .builder()
        .master("local[2]")
        .appName("Simple Application").orCreate
```

This is not needed when running the Kotlin Spark API from a Jupyter notebook.

### Creating a Dataset in Kotlin
```kotlin
spark.dsOf("a" to 1, "b" to 2)
```
The example above produces `Dataset<Pair<String, Int>>`. While Kotlin Pairs and Triples are supported, Scala Tuples are 
recommended for better support.
 
### Null safety
There are several aliases in API, like `leftJoin`, `rightJoin` etc. These are null-safe by design. 
For example, `leftJoin` is aware of nullability and returns `Dataset<Pair<LEFT, RIGHT?>>`.
Note that we are forcing `RIGHT` to be nullable for you as a developer to be able to handle this situation. 
`NullPointerException`s are hard to debug in Spark, and we doing our best to make them as rare as possible.

### withSpark function

We provide you with useful function `withSpark`, which accepts everything that may be needed to run Spark — properties, name, master location and so on. It also accepts a block of code to execute inside Spark context.

After work block ends, `spark.stop()` is called automatically.

Do not use this when running the Kotlin Spark API from a Jupyter notebook.

```kotlin
withSpark {
    dsOf(1, 2)
        .map { it X it } // creates Tuple2<Int, Int>
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
        .map { tupleOf(it, it + 2) }
        .withCached {
            showDS()
  
            filter { it._1 % 2 == 0 }.showDS()
        }
        .map { tupleOf(it._1, it._2, (it._1 + it._2) * 2) }
        .show()
}
```

Here we're showing cached `Dataset` for debugging purposes then filtering it. 
The `filter` method returns filtered `Dataset` and then the cached `Dataset` is being unpersisted, so we have more memory t
o call the `map` method and collect the resulting `Dataset`.

### toList and toArray methods

For more idiomatic Kotlin code we've added `toList` and `toArray` methods in this API. You can still use the `collect` method as in Scala API, however the result should be casted to `Array`.
  This is because `collect` returns a Scala array, which is not the same as Java/Kotlin one.

### Column infix/operator functions

Similar to the Scala API for `Columns`, many of the operator functions could be ported over.
For example:
```kotlin
dataset.select( col("colA") + 5 )
dataset.select( col("colA") / col("colB") )

dataset.where( col("colA") `===` 6 )
// or alternatively
dataset.where( col("colA") eq 6)
```

To read more, check the [wiki](https://github.com/JetBrains/kotlin-spark-api/wiki/Column-functions).

### Overload resolution ambiguity

We had to implement the functions `reduceGroups` and `reduce` for Kotlin separately as `reduceGroupsK` and `reduceK` respectively, because otherwise it caused resolution ambiguity between Kotlin, Scala and Java APIs, which was quite hard to solve.

We have a special example of work with this function in the [Groups example](https://github.com/JetBrains/kotlin-spark-api/blob/main/examples/src/main/kotlin/org/jetbrains/kotlinx/spark/examples/Group.kt).

### Tuples

Inspired by [ScalaTuplesInKotlin](https://github.com/Jolanrensen/ScalaTuplesInKotlin), the API introduces a lot of helper- extension functions
to make working with Scala Tuples a breeze in your Kotlin Spark projects. While working with data classes is encouraged,
for pair-like Datasets / RDDs / DStreams Scala Tuples are recommended, both for the useful helper functions, as well as Spark performance.
To enable these features
simply add
```kotlin
import org.jetbrains.kotlinx.spark.api.tuples.*
```
to the start of your file.

Tuple creation can be done in the following manners:
```kotlin
val a: Tuple2<Int, Long> = tupleOf(1, 2L)
val b: Tuple3<String, Double, Int> = t("test", 1.0, 2)
val c: Tuple3<Float, String, Int> = 5f X "aaa" X 1
```
To read more about tuples and all the added functions, refer to the [wiki](https://github.com/JetBrains/kotlin-spark-api/wiki/Tuples).

### Streaming

A popular Spark extension is [Spark Streaming](https://spark.apache.org/docs/latest/streaming-programming-guide.html). 
Of course the Kotlin Spark API also introduces a more Kotlin-esque approach to write your streaming programs.
There are examples for use with a checkpoint, Kafka and SQL in the [examples module](examples/src/main/kotlin/org/jetbrains/kotlinx/spark/examples/streaming).

We shall also provide a quick example below:
```kotlin
// Automatically provides ssc: JavaStreamingContext which starts and awaits termination or timeout
withSparkStreaming(batchDuration = Durations.seconds(1), timeout = 10_000) { // this: KSparkStreamingSession

    // create input stream for, for instance, Netcat: `$ nc -lk 9999`
    val lines: JavaReceiverInputDStream<String> = ssc.socketTextStream("localhost", 9999)
  
    // split input stream on space
    val words: JavaDStream<String> = lines.flatMap { it.split(" ").iterator() }

    // perform action on each formed RDD in the stream
    words.foreachRDD { rdd: JavaRDD<String>, _: Time ->
      
          // to convert the JavaRDD to a Dataset, we need a spark session using the RDD context
          withSpark(rdd) { // this: KSparkSession
            val dataframe: Dataset<TestRow> = rdd.map { TestRow(word = it) }.toDS()
            dataframe
                .groupByKey { it.word }
                .count()
                .show()
            // +-----+--------+
            // |  key|count(1)|
            // +-----+--------+
            // |hello|       1|
            // |   is|       1|
            // |    a|       1|
            // | this|       1|
            // | test|       3|
            // +-----+--------+
        }
    }
}
```

For more information, check the [wiki](https://github.com/JetBrains/kotlin-spark-api/wiki/Streaming).

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



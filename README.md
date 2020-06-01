# Kotlin Spark API

---

Your next API to work with  [Spark](https://spark.apache.org/)

One day this should become part of https://github.com/apache/spark of repository, consider this as beta-quality software.

## Goal

This project adds missing layer of compatibility between [Kotlin](https://kotlinlang.org/) and [Spark](https://spark.apache.org/).

Despite Kotlin having first-class compatibility API Kotlin developers might want to use familiar features like data 
classes and lambda expressions as simple expressions in curly braces or method references.

## Non-goals

There is no goal to replace any currently supported language or provide them with some functionality to support Kotlin 
language.

## Installation

Currently, there are no kotlin-spark-api artifacts in maven central, but you can obain copy using JitPack here: 
[![](https://jitpack.io/v/JetBrains/kotlin-spark-api.svg)](https://jitpack.io/#JetBrains/kotlin-spark-api)

There is support for `Maven`, `Gradle`, `SBT` and `leinengen` on JitPack.

This project does not force you to use any concrete version of spark, but we've only tested it with spark `3.0.0-preview2`.
We believe it should also work fine with version `2.4.5`

So if you're using Maven you'll hve to add following into your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
<dependency>
    <groupId>com.github.JetBrains.kotlin-spark-api</groupId>
    <artifactId>core</artifactId>
    <version>${kotlin-spark-api.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-sql_2.12</artifactId>
    <version>2.4.5</version>
</dependency>
```

`core` is being compiled against Scala version `2.12` and it means you have to use `2.12` build of spark if you want to 
try out this project.

## Usage

First (and hopefully last) thing you need to do is to add following import to your Kotlin file:

```kotlin
import org.jetbrains.spark.api.*
```

Then you can create SparkSession we all remember and love

```kotlin
val spark = SparkSession
        .builder()
        .master("local[2]")
        .appName("Simple Application").orCreate

```

To create Dataset you may call `toDS` method like this

```kotlin
spark.toDS("a" to 1, "b" to 2)
```

Indeed, this produces `Dataset<Pair<String, Int>>`. There are a couple more `toDS` methods which accept different arguments.

Also, there are several interesting aliases in API, like `leftJoin`, `rightJoin` etc.
Interesting fact about them that they're null-safe by design. For example, `leftJoin` is aware of nullability and returns 
`Dataset<Pair<LEFT, RIGHT?>>`.
Note that were forcing `RIGHT` to be nullable for you as developer to be able to handle this situation.

We know that `NullPointerException`s are hard to debug in Spark And trying hard to make them happen as rare as possible.

## Useful helper methods

### `withSpark`

We provide you with useful function `withSpark`, which accepts everything that may be needed to run spark — properties, 
name, master location and so on. Also it accepts block, which should be launched in spark context.

After work block ends `spark.stop()` is called automatically.

```kotlin
withSpark {
    dsOf(1, 2)
            .map { it to it }
            .show()
}
```

`dsOf` is just one more way to create `Dataset` (`Dataset<Int>`) from varargs.

### `withCached`

It may easily happen that we need to fork our computation to several paths. To compute things only once we should call `cache`
method. But there it is hard to control when we're using cached `Dataset` and when not.
Also it's easy to forget to unpersist cached data, which may make things to break unexpectadle or just take more memory
then intended.

To solve these problems we introduce `withCached` function

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

Here we're showing cached `Dataset` for debugging purposes then filtering it. `filter` method returns filtered `Dataset` 
and then cached `Dataset` is being unpersisted so we have more memory to call `map` method and collect resulting `Dataset`.

## Examples

You cn find more examples in [examples](https://github.com/JetBrains/kotlin-spark-api/tree/master/examples/src/main/kotlin/org/jetbrains/spark/api/examples) module.

## Issues and feedback

Issues and any feedback are very welcome in `Issues` here.

If you find that we missed some important feature — please report and we'll consider adding it.
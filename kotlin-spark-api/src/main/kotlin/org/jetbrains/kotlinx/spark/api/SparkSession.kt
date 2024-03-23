/*-
 * =LICENSE=
 * Kotlin Spark API
 * ----------
 * Copyright (C) 2019 - 2020 JetBrains
 * ----------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =LICENSEEND=
 */

/**
 * This file contains the main entry points and wrappers for the Kotlin Spark API.
 */

@file:Suppress("UsePropertyAccessSyntax")

package org.jetbrains.kotlinx.spark.api


import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaRDDLike
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession.Builder
import org.apache.spark.sql.UDFRegistration
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.jetbrains.kotlinx.spark.api.SparkLogLevel.ERROR
import org.jetbrains.kotlinx.spark.api.tuples.*
import scala.reflect.ClassTag
import java.io.Serializable

/**
 * This wrapper over [SparkSession] which provides several additional methods to create [org.apache.spark.sql.Dataset].
 *
 *  @param spark The current [SparkSession] to wrap
 */
class KSparkSession(val spark: SparkSession) {

    val sparkVersion = /*$"\""+spark+"\""$*/ /*-*/ "nope"

    /** Lazy instance of [JavaSparkContext] wrapper around [sparkContext]. */
    val sc: JavaSparkContext by lazy { JavaSparkContext(spark.sparkContext) }

    /** Utility method to create dataset from list. */
    inline fun <reified T> List<T>.toDS(): Dataset<T> = toDS(spark)

    /** Utility method to create dataframe from list. */
    inline fun <reified T> List<T>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)

    /** Utility method to create dataset from [Array]. */
    inline fun <reified T> Array<T>.toDS(): Dataset<T> = toDS(spark)

    /** Utility method to create dataframe from [Array]. */
    inline fun <reified T> Array<T>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)

    /** Utility method to create dataset from vararg arguments. */
    inline fun <reified T> dsOf(vararg arg: T): Dataset<T> = spark.dsOf(*arg)

    /** Creates new empty dataset of type [T]. */
    inline fun <reified T> emptyDataset(): Dataset<T> = spark.emptyDataset(kotlinEncoderFor<T>())

    /** Utility method to create dataframe from *array or vararg arguments */
    inline fun <reified T> dfOf(vararg arg: T): Dataset<Row> = spark.dfOf(*arg)

    /**Utility method to create dataframe from *array or vararg arguments with given column names */
    inline fun <reified T> dfOf(colNames: Array<String>, vararg arg: T): Dataset<Row> = spark.dfOf(colNames, *arg)

    /** Utility method to create dataset from Scala [RDD]. */
    inline fun <reified T> RDD<T>.toDS(): Dataset<T> = toDS(spark)

    /** Utility method to create dataset from [JavaRDDLike]. */
    inline fun <reified T> JavaRDDLike<T, *>.toDS(): Dataset<T> = toDS(spark)

    /**
     * Utility method to create Dataset<Row> (Dataframe) from RDD.
     * NOTE: [T] must be [Serializable].
     */
    inline fun <reified T> RDD<T>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)

    /**
     * Utility method to create Dataset<Row> (Dataframe) from JavaRDD.
     * NOTE: [T] must be [Serializable].
     */
    inline fun <reified T> JavaRDDLike<T, *>.toDF(vararg colNames: String): Dataset<Row> = toDF(spark, *colNames)

    /**
     * Utility method to create an RDD from a list.
     * NOTE: [T] must be [Serializable].
     */
    fun <T> List<T>.toRDD(numSlices: Int = sc.defaultParallelism()): JavaRDD<T> =
        sc.toRDD(this, numSlices)

    /**
     * Utility method to create an RDD from a list.
     * NOTE: [T] must be [Serializable].
     */
    fun <T> rddOf(vararg elements: T, numSlices: Int = sc.defaultParallelism()): JavaRDD<T> =
        sc.toRDD(elements.toList(), numSlices)

    /**
     * A collection of methods for registering user-defined functions (UDF).
     *
     * The following example registers a UDF in Kotlin:
     * ```Kotlin
     *   sparkSession.udf.register("myUDF") { arg1: Int, arg2: String -> arg2 + arg1 }
     * ```
     *
     * @note The user-defined functions must be deterministic. Due to optimization,
     * duplicate invocations may be eliminated or the function may even be invoked more times than
     * it is present in the query.
     */
    val udf: UDFRegistration get() = spark.udf()

    inline fun <RETURN, reified NAMED_UDF : NamedUserDefinedFunction<RETURN, *>>
            NAMED_UDF.register(): NAMED_UDF =
        this@KSparkSession.udf.register(namedUdf = this)

    inline fun <RETURN, reified NAMED_UDF : NamedUserDefinedFunction<RETURN, *>>
            UserDefinedFunction<RETURN, NAMED_UDF>.register(name: String): NAMED_UDF =
        this@KSparkSession.udf.register(name = name, udf = this)
}

/**
 * This wrapper over [SparkSession] and [JavaStreamingContext] provides several additional methods to create [org.apache.spark.sql.Dataset]
 */
class KSparkStreamingSession(@Transient val ssc: JavaStreamingContext) : Serializable {
    // Serializable and Transient so that [withSpark] works inside [foreachRDD] and other Spark functions that serialize

    private var runAfterStart: KSparkStreamingSession.() -> Unit = {}

    /** [block] will be run after the streaming session has started from a new context (so not when loading from a checkpoint)
     *  and before it's terminated. */
    fun setRunAfterStart(block: KSparkStreamingSession.() -> Unit) {
        runAfterStart = block
    }

    fun invokeRunAfterStart(): Unit = runAfterStart()


    /** Creates new spark session from given [sc]. */
    fun getSpark(sc: SparkConf): SparkSession =
        SparkSession
            .builder()
            .config(sc)
            .getOrCreate()

    /** Creates new spark session from context of given JavaRDD, [rddForConf]. */
    fun getSpark(rddForConf: JavaRDDLike<*, *>): SparkSession = getSpark(rddForConf.context().conf)

    /** Creates new spark session from context of given JavaStreamingContext, [sscForConf] */
    fun getSpark(sscForConf: JavaStreamingContext): SparkSession = getSpark(sscForConf.sparkContext().conf)

    /**
     * Helper function to enter Spark scope from [sc] like
     * ```kotlin
     * withSpark(sc) { // this: KSparkSession
     *
     * }
     * ```
     */
    fun <T> withSpark(sc: SparkConf, func: KSparkSession.() -> T): T =
        KSparkSession(getSpark(sc)).func()

    /**
     * Helper function to enter Spark scope from a provided like
     * when using the `foreachRDD` function.
     * ```kotlin
     * withSpark(rdd) { // this: KSparkSession
     *
     * }
     * ```
     */
    fun <T> withSpark(rddForConf: JavaRDDLike<*, *>, func: KSparkSession.() -> T): T =
        KSparkSession(getSpark(rddForConf)).func()

    /**
     * Helper function to enter Spark scope from [sscForConf] like
     * ```kotlin
     * withSpark(ssc) { // this: KSparkSession
     *
     * }
     * ```
     */
    fun <T> withSpark(sscForConf: JavaStreamingContext, func: KSparkSession.() -> T): T =
        KSparkSession(getSpark(sscForConf)).func()
}


/**
 * The entry point to programming Spark with the Dataset and DataFrame API.
 *
 * @see org.apache.spark.sql.SparkSession
 */
typealias SparkSession = org.apache.spark.sql.SparkSession

/**
 * Control our logLevel. This overrides any user-defined log settings.
 * @param level The desired log level as [SparkLogLevel].
 */
fun SparkContext.setLogLevel(level: SparkLogLevel): Unit = setLogLevel(level.name)

/** Log levels for spark. */
enum class SparkLogLevel {
    ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
}

/**
 * Returns the Spark context associated with this Spark session.
 */
val SparkSession.sparkContext: SparkContext
    get() = sparkContext()

/**
 * Wrapper for spark creation which allows setting different spark params.
 *
 * @param props spark options, value types are runtime-checked for type-correctness
 * @param master Sets the Spark master URL to connect to, such as "local" to run locally, "local[4]" to
 *  run locally with 4 cores, or "spark://master:7077" to run on a Spark standalone cluster. By default, it
 *  tries to get the system value "spark.master", otherwise it uses "local[*]"
 * @param appName Sets a name for the application, which will be shown in the Spark web UI.
 *  If no application name is set, a randomly generated name will be used.
 * @param logLevel Control our logLevel. This overrides any user-defined log settings.
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */
@JvmOverloads
inline fun withSpark(
    props: Map<String, Any> = emptyMap(),
    master: String = SparkConf().get("spark.master", "local[*]"),
    appName: String = "Kotlin Spark Sample",
    logLevel: SparkLogLevel = ERROR,
    func: KSparkSession.() -> Unit,
) {
    val builder = SparkSession
        .builder()
        .master(master)
        .appName(appName)
        .apply {
            props.forEach {
                when (val value = it.value) {
                    is String -> config(it.key, value)
                    is Boolean -> config(it.key, value)
                    is Long -> config(it.key, value)
                    is Double -> config(it.key, value)
                    else -> throw IllegalArgumentException("Cannot set property ${it.key} because value $value of unsupported type ${value::class}")
                }
            }
        }
    withSpark(builder, logLevel, func)

}

/**
 * Wrapper for spark creation which allows setting different spark params.
 *
 * @param builder A [SparkSession.Builder] object, configured how you want.
 * @param logLevel Control our logLevel. This overrides any user-defined log settings.
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */

@JvmOverloads
inline fun withSpark(builder: Builder, logLevel: SparkLogLevel = ERROR, func: KSparkSession.() -> Unit) {
    builder
        .getOrCreate()
        .apply {
            KSparkSession(this).apply {
                sparkContext.setLogLevel(logLevel)
                func()
                spark.stop()
            }
        }
}

/**
 * Wrapper for spark creation which copies params from [sparkConf].
 *
 * @param sparkConf Sets a list of config options based on this.
 * @param logLevel Control our logLevel. This overrides any user-defined log settings.
 * @param func function which will be executed in context of [KSparkSession] (it means that `this` inside block will point to [KSparkSession])
 */
@JvmOverloads
inline fun withSpark(sparkConf: SparkConf, logLevel: SparkLogLevel = ERROR, func: KSparkSession.() -> Unit) {
    withSpark(
        builder = SparkSession.builder().config(sparkConf),
        logLevel = logLevel,
        func = func,
    )
}


/**
 * Wrapper for spark streaming creation. `spark: SparkSession` and `ssc: JavaStreamingContext` are provided, started,
 * awaited, and stopped automatically.
 * The use of a checkpoint directory is optional.
 * If checkpoint data exists in the provided `checkpointPath`, then StreamingContext will be
 * recreated from the checkpoint data. If the data does not exist, then the provided factory
 * will be used to create a JavaStreamingContext.
 *
 * @param batchDuration         The time interval at which streaming data will be divided into batches. Defaults to 1
 *                              second.
 * @param checkpointPath        If checkpoint data exists in the provided `checkpointPath`, then StreamingContext will be
 *                              recreated from the checkpoint data. If the data does not exist (or `null` is provided),
 *                              then the streaming context will be built using the other provided parameters.
 * @param hadoopConf            Only used if [checkpointPath] is given. Hadoop configuration if necessary for reading from
 *                              any HDFS compatible file system.
 * @param createOnError         Only used if [checkpointPath] is given. Whether to create a new JavaStreamingContext if
 *                              there is an error in reading checkpoint data.
 * @param props                 Spark options, value types are runtime-checked for type-correctness.
 * @param master                Sets the Spark master URL to connect to, such as "local" to run locally, "local[4]" to
 *                              run locally with 4 cores, or "spark://master:7077" to run on a Spark standalone cluster.
 *                              By default, it tries to get the system value "spark.master", otherwise it uses "local[*]".
 * @param appName               Sets a name for the application, which will be shown in the Spark web UI.
 *                              If no application name is set, a randomly generated name will be used.
 * @param timeout               The time in milliseconds to wait for the stream to terminate without input. -1 by default,
 *                              this means no timeout.
 * @param startStreamingContext Defaults to `true`. If set to `false`, then the streaming context will not be started.
 * @param func                  Function which will be executed in context of [KSparkStreamingSession] (it means that
 *                              `this` inside block will point to [KSparkStreamingSession])
 */
@JvmOverloads
fun withSparkStreaming(
    batchDuration: Duration = Durations.seconds(1L),
    checkpointPath: String? = null,
    hadoopConf: Configuration = getDefaultHadoopConf(),
    createOnError: Boolean = false,
    props: Map<String, Any> = emptyMap(),
    master: String = SparkConf().get("spark.master", "local[*]"),
    appName: String = "Kotlin Spark Sample",
    timeout: Long = -1L,
    startStreamingContext: Boolean = true,
    func: KSparkStreamingSession.() -> Unit,
) {

    // will only be set when a new context is created
    var kSparkStreamingSession: KSparkStreamingSession? = null

    val creatingFunc = {
        val sc = SparkConf()
            .setAppName(appName)
            .setMaster(master)
            .setAll(
                props
                    .map { (key, value) -> key X value.toString() }
                    .asScalaIterable()
            )

        val ssc = JavaStreamingContext(sc, batchDuration)
        ssc.checkpoint(checkpointPath)

        kSparkStreamingSession = KSparkStreamingSession(ssc)
        func(kSparkStreamingSession!!)

        ssc
    }

    val ssc = when {
        checkpointPath != null ->
            JavaStreamingContext.getOrCreate(checkpointPath, creatingFunc, hadoopConf, createOnError)

        else -> creatingFunc()
    }

    if (startStreamingContext) {
        ssc.start()
        kSparkStreamingSession?.invokeRunAfterStart()
    }
    ssc.awaitTerminationOrTimeout(timeout)
    ssc.stop()
}

// calling org.apache.spark.deploy.`SparkHadoopUtil$`.`MODULE$`.get().conf()
private fun getDefaultHadoopConf(): Configuration {
    val klass = Class.forName("org.apache.spark.deploy.SparkHadoopUtil$")
    val moduleField = klass.getField("MODULE$").also { it.isAccessible = true }
    val module = moduleField.get(null)
    val getMethod = klass.getMethod("get").also { it.isAccessible = true }
    val sparkHadoopUtil = getMethod.invoke(module)
    val confMethod = sparkHadoopUtil.javaClass.getMethod("conf").also { it.isAccessible = true }
    val conf = confMethod.invoke(sparkHadoopUtil) as Configuration

    return conf
}

/**
 * Broadcast a read-only variable to the cluster, returning a
 * [org.apache.spark.broadcast.Broadcast] object for reading it in distributed functions.
 * The variable will be sent to each cluster only once.
 *
 * @param value value to broadcast to the Spark nodes
 * @return `Broadcast` object, a read-only variable cached on each machine
 */
inline fun <reified T> SparkSession.broadcast(value: T): Broadcast<T> = try {
    sparkContext.broadcast(value, ClassTag.apply(T::class.java))
} catch (e: ClassNotFoundException) {
    JavaSparkContext(sparkContext).broadcast(value)
}

/**
 * Broadcast a read-only variable to the cluster, returning a
 * [org.apache.spark.broadcast.Broadcast] object for reading it in distributed functions.
 * The variable will be sent to each cluster only once.
 *
 * @param value value to broadcast to the Spark nodes
 * @return `Broadcast` object, a read-only variable cached on each machine
 * @see broadcast
 */
@Deprecated(
    "You can now use `spark.broadcast()` instead.",
    ReplaceWith("spark.broadcast(value)"),
    DeprecationLevel.WARNING
)
inline fun <reified T> SparkContext.broadcast(value: T): Broadcast<T> = try {
    broadcast(value, ClassTag.apply(T::class.java))
} catch (e: ClassNotFoundException) {
    JavaSparkContext(this).broadcast(value)
}
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.SparkSession

object Main {
    data class Q(val id: Int, val text: String)

    @JvmStatic
    fun main(args: Array<String>) {
        println("hello")
        val logFile = "/Users/vitaly.khudobakhshov/Documents/scaladays2019.txt"
        val spark = SparkSession
                .builder()
                .master("local[2]")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .appName("Simple Application").orCreate

//        val logData = spark.read().textFile(logFile).cache()
//
//        val numAs = logData.filter { s -> s.contains("a") }.count()
//        val numBs = logData.filter { s -> s.contains("b") }.count()
//
//        println("Lines with a: $numAs, lines with b: $numBs")

        val ds = spark
                .toDS(listOf(1, 2, 3))
                .map { Pair(it, it + 1) }
//
        println("TEST >>>" + ds)
//        val enc = KotlinEncoder.bean(Pair::class.java)
        val enc = Encoders.kryo(Pair::class.java)
//
        println(">>>  CT=" + enc.clsTag())
        println(">>>  SC=" + enc.schema())

//        val jsc = JavaSparkContext(spark.sparkContext())
//        val rdd = jsc.parallelize(listOf(Q(1, "hello"), Q(2, "world")))
//        println(rdd.take(1))
        spark.stop()
    }
}
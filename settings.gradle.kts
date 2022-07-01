val spark: String by settings
val scala: String by settings
val skipScalaTuplesInKotlin: String by settings
System.setProperty("spark", spark)
System.setProperty("scala", scala)
System.setProperty("skipScalaTuplesInKotlin", skipScalaTuplesInKotlin)


val scalaCompat
    get() = scala.substringBeforeLast('.')

val versions = "${scalaCompat}_$spark"

rootProject.name = "kotlin-spark-api-parent_$versions"

include("core")
include("scala-tuples-in-kotlin")
include("kotlin-spark-api")
include("jupyter")
include("examples")

//project(":core").name = "core_$versions"
//project(":scala-tuples-in-kotlin").name = "scala-tuples-in-kotlin_$scalaCompat"
//project(":kotlin-spark-api").name = "kotlin-spark-api_$versions"
//project(":jupyter").name = "jupyter_$versions"
//project(":examples").name = "examples_$versions"

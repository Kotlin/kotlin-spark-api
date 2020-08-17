# Quick Start Guide

This tutorial provides instructions to help you get started with Kotlin Spark API. We use an example similar to the official [Apache Spark 
Quick Start Guide](https://spark.apache.org/docs/3.0.0/quick-start.html#self-contained-applications). 
You'll learn what you need to set up your environment, how to write, package and execute a simple self-contained application.
 
Prerequisites:
- You need to have Java installed and have the JAVA_HOME environment variable pointing to the Java installation.
- You need to have Apache Spark installed and have SPARK_HOME environment variable pointing to the Spark installation. 
We recommend using Apache Spark 3.0.0 version. You can download it from the [Spark official website](https://spark.apache.org/downloads.html).
   

## Self-contained application

For the purposes of this tutorial, let's write a Kotlin program that counts the number of lines containing 'a', 
and the number containing 'b' in the Spark README. Note that you'll need to replace `YOUR_SPARK_HOME` with the 
location where Spark is installed:

```kotlin
/* SimpleApp.kt */
@file:JvmName("SimpleApp")
import org.jetbrains.spark.api.*

fun main() {
    val logFile = "YOUR_SPARK_HOME/README.md" // Change to your Spark Home path
    withSpark {
        spark.read().textFile(logFile).withCached {
            val numAs = filter { it.contains("a") }.count()
            val numBs = filter { it.contains("b") }.count()
            println("Lines with a: $numAs, lines with b: $numBs")
        }
    }
}
``` 

## Building the application
Because Kotlin Spark API is not part of the official Apache Spark distribution yet, it is not enough to add Spark 
as a dependency to your build file. 
You need to: 
- Add Spark as a dependency
- Add Kotlin Spark API as a dependency
- Add Kotlin Standard Library as a dependency

When packaging your project into a jar file, you need to explicitly include Kotlin Spark API and Kotlin Standard Library 
dependencies. Here you can find an example of building your application with Maven, and with Gradle. 

### Building the application with Maven

Here's what the `pom.xml` looks like for this example:
```xml
<project>
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>kotlin-spark-example</artifactId>
    <version>1.0-SNAPSHOT</version>

    <name>Sample Project</name>
    <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <kotlin.version>1.3.72</kotlin.version>
        <kotlin.code.style>official</kotlin.code.style>
    </properties>

    <repositories> <!-- Kotlin Spark API is currently published on jitpack.io -->
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>1.3.72</version>
        </dependency>
        <dependency> <!-- Kotlin Spark API dependency -->
            <groupId>com.github.JetBrains.kotlin-spark-api</groupId>
            <artifactId>kotlin-spark-api</artifactId>
            <version>0.3.1</version>
        </dependency>
        <dependency> <!-- Spark dependency -->
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_2.12</artifactId>
            <version>3.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.4</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <artifactSet>
                                <includes>
                                    <include>com.github.JetBrains.kotlin-spark-api:*</include>
                                    <include>org.jetbrains.kotlin:* </include>
                                </includes>
                            </artifactSet>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>1.3.72</version>
                <configuration>
                    <sourceDirs>src/main/kotlin</sourceDirs>
                    <jvmTarget>1.8</jvmTarget>
                    <myIncremental>true</myIncremental>
                </configuration>
                <executions>
                    <execution>
                        <id>compile</id>
                        <goals> 
                            <goal>compile</goal> 
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```  

Here's what the project structure should look like:
```
./pom.xml
./src
./src/main
./src/main/kotlin
./src/main/kotlin/SimpleApp.kt

```

Now you can package the application using Maven:
`mvn package`

### Building the application with Gradle

Here's what the `build.gradle` looks like for this example:

```
plugins {
  id 'org.jetbrains.kotlin.jvm' version '1.3.72'
  id 'com.github.johnrengelman.shadow' version '5.2.0'
}

group = 'org.example'
version = '1.0-SNAPSHOT'

repositories {
  mavenCentral()
  maven { url = 'https://jitpack.io' }
}

dependencies {
  // Kotlin stdlib
  implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.72'
  // Kotlin Spark API
  implementation 'com.github.JetBrains.kotlin-spark-api:kotlin-spark-api:0.3.1'
  // Apache Spark
  compileOnly 'org.apache.spark:spark-sql_2.12:3.0.0'
}

compileKotlin {
  kotlinOptions.jvmTarget = '1.8'
}

shadowJar {
  dependencies {
    exclude(dependency {
      it.moduleGroup == 'org.apache.spark' || it.moduleGroup == "org.scala-lang"
    })
  }
}
```

build.gradle.kts (Kotlin DSL)
```
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id ("org.jetbrains.kotlin.jvm") version "1.3.72"
  id ("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
  mavenCentral()
  maven (url = "https://jitpack.io")
}

dependencies {
  // Kotlin stdlib
  implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
  // Kotlin Spark API
  implementation ("com.github.JetBrains.kotlin-spark-api:kotlin-spark-api:0.3.1")
  // Apache Spark
  compileOnly ("org.apache.spark:spark-sql_2.12:3.0.0")
}

compileKotlin.kotlinOptions.jvmTarget = "1.8"

tasks {
  named<ShadowJar>("shadowJar") {
    dependencies {
      exclude{
         it.moduleGroup == "org.apache.spark" || it.moduleGroup == "org.scala-lang"
     }
    }
  }
}
```


Now you can package the application using Gradle:
`gradle shadowJar`

 
## Executing the application with spark-submit

Once you have your jar, you can execute the packaged application with `./bin/spark-submit`:

`YOUR_SPARK_HOME/bin/spark-submit --class "SimpleApp" --master local [path to your jar]`

This example is also available as a [GitHub repo](https://github.com/MKhalusova/kotlin-spark-example), feel free to give it a try.

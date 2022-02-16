plugins {
    val kotlinVersion: String by System.getProperties()

    kotlin("jvm") version kotlinVersion
    `maven-publish`
}

val groupID: String by project
val projectVersion: String by project

group = groupID
version = projectVersion

repositories {
    mavenCentral()
}

publishing {

    publications {
        create<MavenPublication>("maven") {
            groupId = groupID
            artifactId = "kotlin-spark-api-parent"
            version = projectVersion

            /*
            TODO not sure where to put this

            <distributionManagement>
                <repository>
                    <id>ossrh</id>
                    <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
                </repository>
                <snapshotRepository>
                    <id>ossrh</id>
                    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                </snapshotRepository>
            </distributionManagement>

             */



            pom {
                description.set("Parent project for Kotlin for Apache Spark")
                name.set("Kotlin Spark API: Parent")
                packaging = "pom"

                url.set("https://maven.apache.org")
                inceptionYear.set("2019")

                organization {
                    name.set("JetBrains")
                    url.set("https://www.jetbrains.com/")
                }

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("asm0dey")
                        name.set("Pasha Finkelshteyn")
                        email.set("asm0dey@jetbrains.com")
                        timezone.set("GMT+3")
                    }
                    developer {
                        id.set("vitaly.khudobakhshov")
                        name.set("Vitaly Khudobakhshov")
                        email.set("vitaly.khudobakhshov@jetbrains.com")
                        timezone.set("GMT+3")
                    }
                    developer {
                        id.set("Jolanrensen")
                        name.set("Jolan Rensen")
                        email.set("jolan.rensen@jetbrains.com")
                        timezone.set("GMT+1")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/JetBrains/kotlin-spark-api.git")
                    url.set("https://github.com/JetBrains/kotlin-spark-api")
                    tag.set("HEAD")
                }

                distributionManagement {
                    this.relocation {  }
                }



            }
        }
    }
}


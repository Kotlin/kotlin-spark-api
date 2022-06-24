import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Javadoc

plugins {
    scala
    kotlin
    jcp

    mavenPublishBase
}

group = Versions.groupID
version = Versions.project

repositories {
    mavenCentral()
}

dependencies {

    with(Dependencies) {
        api(
            scalaLibrary,
            reflect,
        )

        implementation(
            sparkSql,
        )
    }

}

tasks.preprocess {
    sources.set(sourceSets.main.get().scala.srcDirs)
    fileExtensions.set(listOf("java", "scala"))
    vars.set(
        mapOf(
            "scalaCompat" to Versions.scalaCompat,
            "spark" to Versions.spark,
        )
    )
}

task("changeSourceFolder") {
    sourceSets.main.get().scala.setSrcDirs(
        listOf(tasks.preprocess.get().target)
    )
}.dependsOn(tasks.preprocess)

tasks.compileScala.get().dependsOn(tasks.preprocess)

mavenPublishing {
    configure(JavaLibrary(Javadoc()))
}

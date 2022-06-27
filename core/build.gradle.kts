import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.JavadocJar.Javadoc

plugins {
    scala
//    kotlin
    `java-library`
//    jcp

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


//tasks.preprocess {
//    sources.set(sourceSets.main.get().scala.srcDirs)
//    fileExtensions.set(listOf("java", "scala"))
//    vars.set(
//        mapOf(
//            "scalaCompat" to Versions.scalaCompat,
//            "spark" to Versions.spark,
//        )
//    )
//}
//
//val changeSourceFolder by tasks.registering {
//    sourceSets.main
//        .get()
//        .scala
//        .setSrcDirs(
//            listOf(tasks.preprocess.get().target)
//        )
//}
//changeSourceFolder.get().dependsOn(tasks.preprocess)
//
//tasks.compileScala
//    .get()
//    .dependsOn(changeSourceFolder)

mavenPublishing {
    configure(JavaLibrary(Javadoc()))
}

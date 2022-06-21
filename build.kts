#!/usr/bin/env kotlin

import java.io.File

val files: List<File> = File(".").listFiles()!!.toList().filterNot { it.name == "build.kts" }
files
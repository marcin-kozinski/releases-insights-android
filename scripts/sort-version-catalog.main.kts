#!/usr/bin/env kotlin
// Parse arguments.
val mode =
    when (args[0]) {
        "--fix" -> Mode.Fix
        "--dry-run" -> Mode.Check
        else -> throw IllegalArgumentException("You have to specify either --fix or --dry-run")
    }
val versionCatalog = java.io.File(args[1])

val source = versionCatalog.readText()
val sorted =
    source
        // Split into sections on the empty line.
        .split("\n\n")
        .map {
            // Sort each section.
            it.trim().split("\n").sorted().joinToString("\n")
        }
        // Join sections back into a single string.
        .joinToString(separator = "\n\n", postfix = "\n")

when (mode) {
    Mode.Fix -> {
        // Overwrite the version catalog with the sorted version.
        versionCatalog.writeText(sorted)
        println("Done formatting ${versionCatalog.path}")
    }
    Mode.Check -> {
        if (sorted == source) {
            kotlin.system.exitProcess(0)
        } else {
            println(versionCatalog.path)
            kotlin.system.exitProcess(1)
        }
    }
}

enum class Mode {
    Fix,
    Check,
}

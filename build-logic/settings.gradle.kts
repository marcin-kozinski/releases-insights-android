rootProject.name = "build-logic"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        exclusiveContent {
            forRepository { mavenCentral() }
            filter {
                // Kotlin DSL
                includeGroupAndSubgroups("org.jetbrains")
                // External settings plugins
                includeModule("org.danilopianini", "gradle-pre-commit-git-hooks")
            }
        }
    }
}

include(":settings")

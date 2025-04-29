pluginManagement { includeBuild("build-logic") }

plugins {
    id("releasesinsights.dependency-management")
    id("releasesinsights.pre-commit-git-hooks")
}

rootProject.name = "releases-insights-android"

include(":source:releases-insights-api")

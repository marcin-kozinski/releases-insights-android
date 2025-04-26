plugins { id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.23" }

gitHooks {
    preCommit { from { "scripts/pre-commit.sh" } }
    createHooks(overwriteExisting = true)
}

rootProject.name = "releases-insights-android"

include(":source:releases-insights-api")

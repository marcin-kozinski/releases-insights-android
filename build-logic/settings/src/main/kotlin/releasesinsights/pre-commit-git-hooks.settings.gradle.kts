package releasesinsights

plugins { id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.22" }

gitHooks {
    preCommit { from { "scripts/pre-commit.sh" } }
    createHooks(overwriteExisting = true)
}

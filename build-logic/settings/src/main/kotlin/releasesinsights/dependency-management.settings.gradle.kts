@file:Suppress("UnstableApiUsage")

package releasesinsights

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.InclusiveRepositoryContentDescriptor

pluginManagement {
    repositories {
        mavenCentralFilteredTo {
            includeTrustedPublishers()
            includeTrustedPlugins()
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentralFilteredTo {
            includeTrustedPublishers()
            includeTrustedLibraries()
        }
    }
    versionCatalogs {
        // Gradle does this by default:
        // create("libs") { from(files("gradle/libs.versions.toml")) }
        create("ktorLibs") { from("io.ktor:ktor-version-catalog:3.3.2") }
    }
}

fun RepositoryHandler.mavenCentralFilteredTo(
    filter: MavenCentralInclusiveContentDescriptor.() -> Unit
) {
    exclusiveContent {
        forRepository { mavenCentral() }
        filter { MavenCentralInclusiveContentDescriptor(this).filter() }
    }
}

class MavenCentralInclusiveContentDescriptor(wrapped: InclusiveRepositoryContentDescriptor) :
    InclusiveRepositoryContentDescriptor by wrapped {

    fun includeTrustedPublishers() {
        includeGroupAndSubgroups("org.jetbrains")
        includeGroupAndSubgroups("com.google")
        includeGroupAndSubgroups("com.squareup")
        includeGroupAndSubgroups("com.jakewharton")
    }

    fun includeTrustedPlugins() {
        includeDependencyAnalysisPluginWithTransitiveDeps()
        includeGroup("dev.zacsweers.metro")
    }

    private fun includeDependencyAnalysisPluginWithTransitiveDeps() {
        includeGroup("com.autonomousapps.dependency-analysis")
        includeGroup("com.autonomousapps")
        includeGroup("dev.zacsweers.moshix")
        includeModule("com.github.ben-manes.caffeine", "caffeine")
        includeModule("javax.inject", "javax.inject")
        includeModule("org.checkerframework", "checker-qual")
        includeModule("org.sonatype.oss", "oss-parent")
        includeModule("org.jspecify", "jspecify")
    }

    fun includeTrustedLibraries() {
        includeGroup("io.ktor")
        includeGroup("org.slf4j")
        includeGroup("junit")
        includeGroup("org.hamcrest")
        includeGroup("dev.zacsweers.metro")
    }
}

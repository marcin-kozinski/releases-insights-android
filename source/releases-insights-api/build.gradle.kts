plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
            allWarningsAsErrors = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
            api(ktorLibs.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(ktorLibs.client.contentNegotiation)
            implementation(ktorLibs.client.resources)
            implementation(ktorLibs.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(ktorLibs.client.mock)
        }
    }
}

dependencies { platform(libs.kotlinx.coroutines.bom) }

tasks.register<Test>("test") {
    description = "Runs the tests for all targets and creates an aggregated report"
    dependsOn("allTests")
    testClassesDirs = project.objects.fileCollection()
    // Empty list of files. Leaving this null/unset results in an exception.
}

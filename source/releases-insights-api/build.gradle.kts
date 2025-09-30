plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
}

kotlin { compilerOptions { allWarningsAsErrors = true } }

dependencies {
    platform(libs.kotlinx.coroutines.bom)

    api(libs.kotlinx.datetime)
    api(libs.kotlinx.serialization.core)
    api(ktorLibs.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.client.resources)
    implementation(ktorLibs.http)
    implementation(ktorLibs.resources)
    implementation(ktorLibs.serialization)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.utils)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(ktorLibs.client.mock)
    testImplementation(ktorLibs.http)
    testImplementation(ktorLibs.utils)
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
}

kotlin { compilerOptions { allWarningsAsErrors = true } }

dependencies {
    platform(libs.kotlinx.coroutines.bom)

    api(libs.kotlinx.datetime)
    api(ktorLibs.client.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.client.resources)
    implementation(ktorLibs.serialization.kotlinx.json)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(ktorLibs.client.mock)
}

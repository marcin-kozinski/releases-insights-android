plugins {
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.kotlin.jvm) apply false
}

dependencyAnalysis { issues { all { onAny { severity("fail") } } } }

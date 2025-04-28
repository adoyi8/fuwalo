plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
buildscript {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")             // ← JitPack :contentReference[oaicite:0]{index=0}
    }
}

allprojects {
    repositories {
        google()                                // if you use AndroidX/Google libraries
        mavenCentral()
        maven("https://jitpack.io")             // ← JitPack :contentReference[oaicite:1]{index=1}
    }
}
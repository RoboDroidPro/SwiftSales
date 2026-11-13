plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
//    id("com.google.dagger.hilt.android") version "2.57.2"
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.salestracker"
    compileSdk = 36

    defaultConfig {
        // Unique Play Store Application ID (replaces com.example.* to satisfy Google Play publishing policy)
        applicationId = "com.stephentechlabs.swiftsales"
        minSdk = 26
        targetSdk = 36  //updated to 36 to fix play console error
        versionCode = 2
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Enable R8 code shrinking and obfuscation for production security and smaller APK size
            isMinifyEnabled = true
            // Enable unused resource stripping to minimize bundle size
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
}

hilt{
    enableAggregatingTask = false
}

dependencies {

    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    //Navigation
    implementation(libs.androidx.navigation.compose)

    // Add this (BOM will provide the version) Added for icons' unresolved reference
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Hilt ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // Hilt + Jetpack Compose integration
    implementation(libs.androidx.hilt.navigation.compose)

    //added for serialization
    implementation(libs.kotlinx.serialization.json)
}
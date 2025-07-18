plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")

    alias(libs.plugins.hilt)
    kotlin("kapt") // Hilt needs kapt
}

android {
    namespace = "com.anandgaur.smartmediaai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.anandgaur.smartmediaai"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    configurations.all {
        resolutionStrategy {
            force("com.squareup.okhttp3:okhttp:4.10.0")
            force("com.squareup.okhttp3:okhttp-urlconnection:4.10.0")
        }
    }
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

    // Media3 for video playback and text extraction
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")
    implementation("androidx.media3:media3-common:1.3.0")
    implementation("androidx.media3:media3-transformer:1.3.0")

    // Jetpack Compose
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-ai:16.2.0")
    implementation("com.google.firebase:firebase-vertexai")

    implementation ("androidx.compose.material:material-icons-extended:1.6.1")

    implementation("com.github.kotvertolet:youtube-jextractor:0.3.4") {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
        exclude(group = "com.squareup.okhttp3", module = "okhttp-urlconnection")
        exclude(group = "com.squareup.okhttp3", module = "logging-interceptor")
    }

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
     implementation("com.squareup.okhttp3:okhttp-urlconnection:4.10.0")
     implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.2")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.1") // use latest version
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("com.google.mlkit:genai-image-description:1.0.0-beta1")


    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
}
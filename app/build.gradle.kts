plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.supermarketlistapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.supermarketlistapp"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    }

    // 🎯 IMPORTANTE: Agregar la configuración del compilador de Compose
    composeOptions {
        // Usa una versión reciente y estable
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    // Dependencias base de Kotlin y AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ===============================================
    // 🌐 DEPENDENCIAS AÑADIDAS PARA API, MVVM y UI
    // ===============================================

    // 1. Retrofit (Cliente HTTP para la API de Node.js)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // 2. Convertidor Gson (Para parsear el JSON del API)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 3. ViewModel para Compose (Para usar el ViewModel en las composables)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 4. Corrutinas (Manejo asíncrono, esencial para Retrofit)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 5. Coil (Cargador de imágenes simple, útil si se usa)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ===============================================

    // Dependencias de Compose UI/Material
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Dependencias de testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
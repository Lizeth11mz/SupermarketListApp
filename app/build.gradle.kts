// Importaciones necesarias para manejar archivos y propiedades
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// =========================================================================================
// 🎯 CONFIGURACIÓN DE FIRMA: CARGA DE PROPIEDADES (1/3)
// Carga las contraseñas del archivo keystore.properties de la raíz del proyecto.
// =========================================================================================
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    FileInputStream(keystorePropertiesFile).use {
        keystoreProperties.load(it)
    }
}
// =========================================================================================


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

    // =========================================================================================
    // 🎯 CONFIGURACIÓN DE FIRMA: DEFINICIÓN DE SIGNINGCONFIGS (2/3)
    // Definimos la configuración 'release' para que use las propiedades cargadas.
    // =========================================================================================
    signingConfigs {
        create("release") {
            if (keystoreProperties.containsKey("storeFile")) {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    // =========================================================================================

    buildTypes {
        release {
            // 🎯 CONFIGURACIÓN DE FIRMA: APLICACIÓN DE LA FIRMA (3/3)
            // Asigna la configuración de firma 'release' al tipo de compilación 'release'.
            signingConfig = signingConfigs.getByName("release")

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
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Datos de la firma del APK (archivo keystore.properties en la raíz del proyecto)
val keystoreProps = Properties().apply {
    val archivo = rootProject.file("keystore.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.example.evaluacion2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.evaluacion2"
        minSdk = 24
        // targetSdk 36: Android 17 (API 37) bloquea por defecto el acceso a la red local (10.0.2.2)
        // sin el permiso ACCESS_LOCAL_NETWORK. Con 36 la app mantiene acceso al microservicio.
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Firma digital del APK de release con el archivo .jks
    signingConfigs {
        create("release") {
            storeFile = rootProject.file(keystoreProps.getProperty("storeFile", "keystore/evaluacion2.jks"))
            storePassword = keystoreProps.getProperty("storePassword")
            keyAlias = keystoreProps.getProperty("keyAlias")
            keyPassword = keystoreProps.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            // Genera el reporte de cobertura: gradlew createDebugUnitTestCoverageReport
            enableUnitTestCoverage = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            // Optimización R8 desactivada: el modo experimental "packageScope" (optimizar solo algunas
            // librerías) provocaba IllegalAccessError al iniciar la app. El APK queda firmado igual.
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        // android.util.Log devuelve valores por defecto en las pruebas unitarias (no lanza excepción)
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            // MockK (Byte Buddy) con Java 25: habilita el soporte y evita advertencias en consola
            it.systemProperty("net.bytebuddy.experimental", "true")
            it.jvmArgs("-XX:+EnableDynamicAgentLoading")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.navigation.compose)
    ksp(libs.androidx.room.compiler)

    // Retrofit & OkHttp para consumo de API REST
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    testImplementation(libs.junit)
    // Pruebas unitarias: MockK para simular dependencias y coroutines-test para funciones suspend
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
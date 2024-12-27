plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.chaquo)
    alias(libs.plugins.gms)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.plugin.serialization)
}

android {
    namespace = "com.example.taxonomy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.taxonomy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil)
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

chaquopy {
    defaultConfig {
        buildPython("C:/Users/user/AppData/Local/Programs/Python/Python38/python.exe")

        pyc {
            src = true
        }

        pip {
            install("numpy")

            install("spacy")
//            install("requests==3.7.5")

//            install("sparknlp")
//            install("pyspark")
//            install("pandas")

//            // An sdist or wheel filename, relative to the project directory:
//            install("en_core_web_sm-3.7.1-py3-none-any.whl")
//
//            // A directory containing a setup.py, relative to the project
//            // directory (must contain at least one slash):
            install("./en_core_web_sm-2.2.5")
//
//            // "-r"` followed by a requirements filename, relative to the
//            // project directory:
//            install("-r", "requirements.txt")
        }
    }
}
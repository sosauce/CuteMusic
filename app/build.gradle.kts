plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.sosauce.cutemusic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sosauce.cutemusic"
        minSdk = 26
        targetSdk = 35
        versionCode = 13
        versionName = "2.2.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        kotlinOptions {
            jvmTarget = "17"
        }
        buildFeatures {
            compose = true
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }


//    splits {
//        abi {
//            isEnable = true
//            reset()
//            include("armeabi-v7a", "arm64-v8a")
//            isUniversalApk = false
//        }
//    }
    }

    dependencies {
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.ui)
        implementation(libs.androidx.material.icons.extended)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.core.splashscreen)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.coil.compose)
        implementation(libs.androidx.media3.common)
        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.session)
        implementation(libs.squigglyslider)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(libs.androidx.compose.animation)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.koin.android)
        implementation(libs.koin.androidx.compose)
        debugImplementation(libs.androidx.ui.tooling)
        //implementation("com.materialkolor:material-kolor:1.7.1")
        implementation(libs.koin.androidx.startup)
        implementation(libs.jaudiotagger)
    }
}

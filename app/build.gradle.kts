plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

val versionNameLocal = "3.0.2"


android {
    namespace = "com.sosauce.cutemusic"
    compileSdk = 36

    defaultConfig {

        //noinspection WrongGradleMethod
        val (major, minor, patch) = versionNameLocal.split(".").map { it.toInt() }

        applicationId = "com.sosauce.cutemusic"
        minSdk = 26
        targetSdk = 36
        versionCode = major * 10000 + minor * 100 + patch // https://proandroiddev.com/quick-tip-auto-generate-your-versioncode-614629f7d3bd
        versionName = versionNameLocal
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a")
        }
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = "CM_$versionNameLocal.apk"
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
        dependenciesInfo {
            includeInApk = false
            includeInBundle = false
        }


//        splits {
//            abi {
//                isEnable = true
//                reset()
//                include("armeabi-v7a", "arm64-v8a")
//                isUniversalApk = true
//            }
//        }


    }

    dependencies {
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.ui)
        implementation(libs.androidx.material.icons.extended)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.core.splashscreen)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.coil.compose)
        implementation(libs.androidx.media3.common)
        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.session)
        implementation(libs.squigglyslider)
        implementation(libs.androidx.compose.animation)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.koin.android)
        implementation(libs.koin.androidx.compose)
        implementation(libs.material.kolor)
        implementation(libs.koin.androidx.startup)
        implementation(libs.taglib)
        debugImplementation(libs.androidx.ui.tooling)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.emoji2.emojipicker)
        implementation(libs.kmpalette.core)
        implementation(libs.androidx.glance)
        implementation(libs.androidx.glance.appwidget)
        implementation(libs.androidx.navigation3.runtime)
        implementation(libs.androidx.navigation3.ui)
        implementation(libs.reorderable)
        ksp(libs.androidx.room.compiler)

    }
}

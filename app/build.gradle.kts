plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.kotlin)
	alias(libs.plugins.compose.compiler)
	kotlin("plugin.serialization") version "2.0.0"
}

android {
	namespace = "com.sosauce.cutemusic"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.sosauce.cutemusic"
		minSdk = 26
		targetSdk = 34
		versionCode = 5
		versionName = "1.2.0"

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
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		compose = true
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
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
	// kotlinx-immutable for immutable collections
	implementation(libs.kotlinx.collections.immutable)
	// debug tooling
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}

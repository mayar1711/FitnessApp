plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id ("com.google.dagger.hilt.android")

}

android {
    namespace = "com.example.fitnessapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fitnessapp"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.location)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.compose.android)
    //navigation
    implementation(libs.androidx.hilt.navigation.compose)
    //health connect
    implementation("androidx.health.connect:connect-client:1.1.0-alpha07")
    implementation("com.google.android.gms:play-services-fitness:21.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")


    //hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)


    // hamcrest
    testImplementation (libs.hamcrest)
    testImplementation (libs.hamcrest.library)
    androidTestImplementation (libs.hamcrest)
    androidTestImplementation (libs.hamcrest.library)

    //MockK
    testImplementation (libs.mockk.android)
    testImplementation (libs.mockk.agent)

    //Robolectric
    testImplementation (libs.robolectric)

    //kotlinx-coroutines
    testImplementation (libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
    androidTestImplementation (libs.org.jetbrains.kotlinx.kotlinx.coroutines.test2)
    testImplementation(kotlin("test"))


    testImplementation ("org.mockito:mockito-core:5.0.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")


}

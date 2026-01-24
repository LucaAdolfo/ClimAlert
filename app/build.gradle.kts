plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") version "1.9.22"
    id("org.jetbrains.kotlin.kapt") version "1.9.22"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.climalert"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.climalert"
        minSdk = 32
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.auth)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    /*
    * Qua si aggiungono nostri import
    *
    *
    * */
    implementation(libs.play.services.location)
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.squareup.okhttp3:okhttp:5.3.0")
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.tickaroo.tikxml:annotation:0.8.13")
    implementation("com.tickaroo.tikxml:core:0.8.13")
    kapt("com.tickaroo.tikxml:processor:0.8.13")
    implementation("com.squareup.okio:okio:3.5.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")


    /*
    *
    * Dipendenze ai
    *
     */
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("androidx.room:room-common:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    implementation("androidx.work:work-runtime:2.11.0")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    /*
    *per Gemini
     */
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")


    /*
    * Per WorkManager
    * */
    implementation("androidx.work:work-runtime:2.11.0")
    implementation("androidx.work:work-multiprocess:2.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

}
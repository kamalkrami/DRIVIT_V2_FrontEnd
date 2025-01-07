import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
}

val ip_address = gradleLocalProperties(rootDir,providers).getProperty("ip_address","")
val cloud_name = gradleLocalProperties(rootDir,providers).getProperty("cloud_name","")
val api_key = gradleLocalProperties(rootDir,providers).getProperty("api_key","")
val api_secret = gradleLocalProperties(rootDir,providers).getProperty("api_secret","")

android {
    namespace = "com.example.drivit_v2_frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.drivit_v2_frontend"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue(
            "string",
            "ip_address",
            "\"" + ip_address + "\""
        )
        resValue(
            "string",
            "cloud_name",
            "\"" + cloud_name + "\""
        )
        resValue(
            "string",
            "api_key",
            "\"" + api_key + "\""
        )
        resValue(
            "string",
            "api_secret",
            "\"" + api_secret + "\""
        )
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
}

dependencies {
    implementation("com.cloudinary:cloudinary-android:3.0.2")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.google.material)
    implementation(libs.volley)
    implementation(libs.kotlin.stdlib)
    implementation(libs.styleabletoast)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
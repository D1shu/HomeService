plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.homeservice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.homeservice"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    //enable view binding
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- Firebase Setup (Updated) ---
    // 1. Firebase BoM (આ બધા Firebase વર્ઝન મેનેજ કરશે)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // 2. Authentication (Auth માટે)
    implementation(libs.firebase.auth)

    // 3. Cloud Firestore (KTX ની હવે જરૂર નથી, આમાં જ આવી જાય)
    implementation("com.google.firebase:firebase-firestore")

    // --------------------------------

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // આ લાઈન કાઢી નાખવી કારણ કે આપણે ઉપર સાચું firestore નાખ્યું છે
    // implementation(libs.firebase.firestore.ktx) <--- REMOVE THIS

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}
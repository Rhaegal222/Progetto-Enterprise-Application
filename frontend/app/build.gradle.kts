plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.android.frontend"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.frontend"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation("androidx.room:room-runtime:2.7.0-alpha04")
    implementation("androidx.compose.material:material:1.7.0-beta03")
    implementation("androidx.compose.material3:material3:1.3.0-beta03")
    implementation("androidx.compose.material:material-icons-extended:1.7.0-beta03")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0-beta03")
    implementation("androidx.navigation:navigation-compose:2.8.0-beta03")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.monitor)
    testImplementation("junit:junit:4.12")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Google Authentification Dependencies
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    androidTestImplementation("junit:junit:4.12")

}

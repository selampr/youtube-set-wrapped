import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    // Use kapt without an explicit version to avoid clashes with already-loaded classpath versions
    kotlin("kapt")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val openAiApiKey = localProperties.getProperty("OPENAI_API_KEY") ?: ""
val youtubeApiKey = localProperties.getProperty("YOUTUBE_API_KEY") ?: ""
val escapedOpenAiApiKey = openAiApiKey.replace("\\", "\\\\").replace("\"", "\\\"")
val escapedYoutubeApiKey = youtubeApiKey.replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "com.selampr.youtube_set_wrapped"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.selampr.youtube_set_wrapped"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "OPENAI_API_KEY", "\"$escapedOpenAiApiKey\"")
        buildConfigField("String", "YOUTUBE_API_KEY", "\"$escapedYoutubeApiKey\"")

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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.jsoup)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lottie.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Force the processor classpath to carry the right Javapoet version
    kapt(libs.javapoet)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.openai.client)
    implementation(libs.okhttp)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.coil.compose)
    implementation(libs.ktor.client.okhttp)

}

kapt {
    correctErrorTypes = true
}

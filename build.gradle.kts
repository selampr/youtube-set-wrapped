// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

buildscript {
    configurations.configureEach {
        // Hilt's aggregator must see a Javapoet with canonicalName(); enforce the version early
        resolutionStrategy.force("com.squareup:javapoet:${libs.versions.javapoet.get()}")
    }
    dependencies {
        classpath("com.squareup:javapoet:${libs.versions.javapoet.get()}")
    }
}

// Ensure every configuration (including annotation processors) uses the Javapoet
// version expected by Hilt/Dagger to avoid NoSuchMethodError on canonicalName().
allprojects {
    configurations.configureEach {
        resolutionStrategy.force("com.squareup:javapoet:${libs.versions.javapoet.get()}")
    }
}

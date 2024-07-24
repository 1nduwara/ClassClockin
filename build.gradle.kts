buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        val navVersion = "2.7.3"
        classpath("com.android.tools.build:gradle:8.1.0") // Update to the latest version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Update to the latest version
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.3")

    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

}
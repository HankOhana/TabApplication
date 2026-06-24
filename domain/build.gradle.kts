import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvmToolchain(11)

    // Android target so the Android app/:ui can consume this module.
    android {
        namespace = "com.henadz.sample.tabapplication.domain"
        compileSdk = 36
        minSdk = 27
    }

    // JVM target for fast, framework-free unit tests of business logic.
    jvm()

    sourceSets {
        // All shared business logic lives here — keep it free of Android/framework imports.
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.koin.test)
        }
    }
}

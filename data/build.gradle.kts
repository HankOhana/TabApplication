plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvmToolchain(11)

    // Android target so the Android app/:ui can consume this module.
    android {
        namespace = "com.henadz.sample.tabapplication.data"
        compileSdk = 36
        minSdk = 29
    }

    // JVM target for fast, framework-free unit tests.
    jvm()

    sourceSets {
        commonMain.dependencies {
            // :data implements interfaces declared in :domain — depend inward only.
            implementation(project(":domain"))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
        }
    }
}

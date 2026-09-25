plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

val versionName: String = (project.findProperty("notez.versionName") as String? ?: "0.1.0")
val versionCode: Int = (project.findProperty("notez.versionCode") as String? ?: "1").toInt()

android {
    namespace = "com.zaba.notez"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zaba.notez"
        minSdk = 26
        targetSdk = 34
        this.versionCode = versionCode
        this.versionName = versionName
    }

    signingConfigs {
        create("release") {
            // Fail-closed HANYA saat task Release diminta (build debug/CI tetap jalan).
            val releaseRequested = gradle.startParameter.taskNames.any {
                it.contains("Release", ignoreCase = true)
            }
            fun need(name: String): String =
                System.getenv(name) ?: if (releaseRequested)
                    throw GradleException("$name belum diset")
                else ""
            val ks = need("NOTEZ_KEYSTORE")
            if (ks.isNotEmpty()) {
                storeFile = file(ks)
                storePassword = need("NOTEZ_STORE_PASS")
                keyAlias = need("NOTEZ_KEY_ALIAS")
                keyPassword = need("NOTEZ_KEY_PASS")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-session:1.3.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Render Markdown (heading, bold/italic, list, code, dsb) di mode view.
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tasklist:4.6.2")
}

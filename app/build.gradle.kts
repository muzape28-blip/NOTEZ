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
            // Fail-closed: tanpa 4 env ini, build release GAGAL dengan pesan jelas.
            // Lokal: export dari ~/.keystores. CI: GitHub secrets (NOTEZ_*).
            val ks = System.getenv("NOTEZ_KEYSTORE")
                ?: throw GradleException("NOTEZ_KEYSTORE belum diset (path keystore rilis)")
            val sp = System.getenv("NOTEZ_STORE_PASS")
                ?: throw GradleException("NOTEZ_STORE_PASS belum diset")
            val ka = System.getenv("NOTEZ_KEY_ALIAS")
                ?: throw GradleException("NOTEZ_KEY_ALIAS belum diset")
            val kp = System.getenv("NOTEZ_KEY_PASS")
                ?: throw GradleException("NOTEZ_KEY_PASS belum diset")
            storeFile = file(ks)
            storePassword = sp
            keyAlias = ka
            keyPassword = kp
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
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}

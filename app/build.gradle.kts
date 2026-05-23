plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.aaosstudy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aaosstudy"
        minSdk = 26
        targetSdk = 34

        val buildNumber =
            (project.findProperty("buildNumber") as String?)?.toIntOrNull() ?: 1
        val buildSha = (project.findProperty("buildSha") as String?) ?: "local"
        val shortSha = buildSha.take(7)

        versionCode = buildNumber
        versionName = "0.1.$buildNumber+$shortSha"
        vectorDrawables { useSupportLibrary = true }

        buildConfigField("String", "GIT_SHA", "\"$shortSha\"")
        buildConfigField(
            "String", "UPDATE_REPO", "\"masakasakasama/AAOS_study\""
        )
    }

    signingConfigs {
        // 固定鍵。全ビルドを同じ鍵で署名 → アンインストール無しで上書き更新できる。
        // 学習用サンプルのため debug 用途の鍵をリポジトリに同梱している。
        create("shared") {
            storeFile = file("keystore/aaosstudy.jks")
            storePassword = "aaosstudy"
            keyAlias = "aaosstudy"
            keyPassword = "aaosstudy"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("shared")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("shared")
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
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
}

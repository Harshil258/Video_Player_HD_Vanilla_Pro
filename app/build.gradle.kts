plugins {
    id("vanillavideoplayer.android.application")
    id("vanillavideoplayer.android.hilt")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro"
/* TODO : Don't Forgot to change the version buddy */
    defaultConfig {
        applicationId = "com.vanillavideoplayer.hd.videoplayer.pro"
        versionCode = 1
        versionName = "1"
    }

//    signingConfigs {
//        create("release") {
//            storeFile = file("C:\\Users\\zeeld\\Desktop\\Android\\Vanilla Player\\Keys\\Vanilla Player Pro.jks")
//            storePassword = "123456789"
//            keyAlias = "Key0"
//            keyPassword = "123456789"
//        }
//    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }


    }
    bundle {
        language {
            enableSplit = false
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:VanillaData"))
    implementation(project(":core:VanillaMedia"))
    implementation(project(":core:BadhaModel"))
    implementation(project(":core:AaakhuUi"))
    implementation(project(":feature:VanillaVideoPicker"))
    implementation(project(":feature:VanillaMainPlayer"))
    implementation(project(":feature:VanillaSettings"))


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)
    implementation("androidx.compose.material3:material3:1.2.1")

    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.timber)
    implementation (libs.gson)
    implementation("com.google.firebase:firebase-core:21.1.1")
    api("com.google.firebase:firebase-core:21.1.1")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.inappmessaging.display)

    implementation(libs.lottie.v640)
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")


    implementation("com.github.Harshil258:AndroidAdPlacer:1.0.51")
    implementation(project(":core:AaakhuUi"))

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.config.ktx)

    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    implementation(libs.billing.ktx)
    implementation(libs.review)

    implementation(libs.glide)
}


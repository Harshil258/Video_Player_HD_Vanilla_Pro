plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.feature.player"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:AaakhuUi"))
    implementation(project(":core:VanillaData"))
    implementation(project(":core:VanillaDomain"))
    implementation(project(":core:BadhaModel"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.google.android.material)
    implementation(libs.androidx.constraintlayout)

    // Media3
    implementation(libs.bundles.media3)
    implementation("org.checkerframework:checker-qual:3.42.0")

    implementation(libs.timber)
//    implementation(project(":AdValuModule"))
    implementation("com.github.Harshil258:AndroidAdPlacer:1.0.51")

//    testImplementation(libs.junit4)
//    androidTestImplementation(libs.androidx.test.ext)
//    androidTestImplementation(libs.androidx.test.espresso.core)

    implementation("com.github.anilbeesetti.nextlib:nextlib-media3ext:0.6.0")
    implementation("com.github.anilbeesetti.nextlib:nextlib-mediainfo:0.6.0")

}

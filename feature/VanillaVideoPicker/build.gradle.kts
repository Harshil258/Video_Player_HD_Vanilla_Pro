plugins {
    id("vanillavideoplayer.android.library")
//    id("vanillavideoplayer.android.library.compose")
    id("vanillavideoplayer.android.hilt")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.vanillavideoplayer.videoplayer.feature.videopicker"
}

dependencies {

    implementation(project(":core:AaakhuUi"))
    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:VanillaData"))
    implementation(project(":core:VanillaDomain"))
    implementation(project(":core:BadhaModel"))
    implementation(project(":feature:VanillaSettings"))


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)
    implementation("androidx.compose.material3:material3:1.2.1")

    implementation(libs.github.skydoves.landscapist.glide)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)


    implementation(libs.androidx.hilt.navigation.compose)
    implementation(project(mapOf("path" to ":feature:VanillaMainPlayer")))
    implementation(project(mapOf("path" to ":feature:VanillaMainPlayer")))
//    implementation(project(mapOf("path" to ":AdValuModule")))
    implementation("com.github.Harshil258:AndroidAdPlacer:1.0.51")

//    testImplementation(libs.junit4)
//    androidTestImplementation(libs.androidx.test.ext)
//    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.compose.ui.test)
    implementation("androidx.compose.ui:ui-test-junit4:1.6.8")

    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.testManifest)
}

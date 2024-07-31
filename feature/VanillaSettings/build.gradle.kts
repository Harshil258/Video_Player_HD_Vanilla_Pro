plugins {
    id("vanillavideoplayer.android.library")
//    id("vanillavideoplayer.android.library.compose")
    id("vanillavideoplayer.android.hilt")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.feature.settings"
}

dependencies {

    implementation(project(":core:AaakhuUi"))
    implementation(project(":core:VanillaData"))
    implementation(project(":core:VanillaDomain"))
    implementation(project(":core:BadhaModel"))
    implementation(project(":core:VanillaCommon"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.compose)
    implementation("androidx.compose.material3:material3:1.2.1")

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.aboutlibraries.compose)
//    implementation(project(mapOf("path" to ":AdValuModule")))
    implementation("com.github.Harshil258:AndroidAdPlacer:1.0.51")

    implementation ("com.google.code.gson:gson:2.11.0")


//    testImplementation(libs.junit4)
//    androidTestImplementation(libs.androidx.test.ext)
//    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.compose.ui.test)
//    implementation("androidx.compose.ui:ui-test-junit4:1.6.8")

    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.testManifest)
    implementation(libs.coil.compose)
}

plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.core.datastore"
}

dependencies {

    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:BadhaModel"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.datastore.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
}

android {
    namespace = "com.vanillavideoplayer.videoplayer.core.media"
}

dependencies {

    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:Khajanchi"))

    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

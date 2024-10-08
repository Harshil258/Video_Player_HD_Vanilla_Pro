plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.core.domain"
}

dependencies {

    implementation(project(":core:VanillaData"))
    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:BadhaModel"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

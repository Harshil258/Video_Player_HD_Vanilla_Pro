plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.core.data"
}

dependencies {

    implementation(project(":core:Khajanchi"))
    implementation(project(":core:VanillaMedia"))
    implementation(project(":core:VanillaCommon"))
    implementation(project(":core:BadhaModel"))
    implementation(project(":core:VanillaDatastore"))

    implementation(libs.timber)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

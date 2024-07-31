plugins {
    id("vanillavideoplayer.android.library")
    id("vanillavideoplayer.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.vanillavideoplayer.hd.videoplayer.pro.core.database"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

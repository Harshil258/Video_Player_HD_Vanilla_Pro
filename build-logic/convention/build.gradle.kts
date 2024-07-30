plugins {
    `kotlin-dsl`
}

group = "com.vanillavideoplayer.videoplayer.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
//        register("androidApplicationCompose") {
//            id = "vanillavideoplayer.android.application.compose"
//            implementationClass = "AppComposeConvention"
//        }
        register("androidApplication") {
            id = "vanillavideoplayer.android.application"
            implementationClass = "AppConvention"
        }
//        register("androidLibraryCompose") {
//            id = "vanillavideoplayer.android.library.compose"
//            implementationClass = "LibComposeConvention"
//        }
        register("androidLibrary") {
            id = "vanillavideoplayer.android.library"
            implementationClass = "LibComposeConvention"
            implementationClass = "LibConvention"
        }
        register("androidHilt") {
            id = "vanillavideoplayer.android.hilt"
            implementationClass = "HiltConvention"
        }
        register("jvmLibrary") {
            id = "vanillavideoplayer.jvm.library"
            implementationClass = "JvmLibConvention"
        }
    }
}
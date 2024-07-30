pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven { url =  uri("https://jitpack.io") }
        maven { url =  uri("https://maven.google.com") }

        maven {
            name = "IronSourceAdsRepo"
            url = uri("https://android-sdk.is.com/")
            content { includeGroup("com.ironsource.sdk") }
        }
        maven {
            name = "MintegralAdsRepo"
            url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
            content { includeGroup("com.mbridge.msdk.oversea") }
        }
        maven {
            name = "PangleAdsRepo"
            url = uri("https://artifact.bytedance.com/repository/pangle")
            content {

            }
            content { includeGroup("com.pangle.global") }
        }


    }
}

rootProject.name = "Video Player HD - Vanilla Pro"
include(":app")
include(":core:VanillaCommon")
include(":core:VanillaData")
include(":core:Khajanchi")
include(":core:VanillaDatastore")
include(":core:VanillaDomain")
include(":core:VanillaMedia")
include(":core:BadhaModel")
include(":core:AaakhuUi")
include(":feature:VanillaMainPlayer")
include(":feature:VanillaSettings")
include(":feature:VanillaVideoPicker")
//include(":AdValuModule")
//include(":AdPlacer")



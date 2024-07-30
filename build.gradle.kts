import com.mikepenz.aboutlibraries.plugin.DuplicateMode
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat


plugins {
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false

}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "com.mikepenz.aboutlibraries.plugin")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
    }

    configure<com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension> {

        excludeFields = arrayOf("generated")
        duplicationMode = DuplicateMode.MERGE
    }
}

allprojects {
    tasks.withType<Test>().configureEach {
        testLogging {
            events = setOf(
                TestLogEvent.PASSED,
                TestLogEvent.FAILED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR
            )

            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
        ignoreFailures = true
    }
}

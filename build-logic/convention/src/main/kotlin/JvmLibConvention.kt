import com.vanillavideoplayer.hd.videoplayer.pro.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            configureKotlin()
        }
    }
}
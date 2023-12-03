rootProject.name = "coding-buddy-plugin"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id in setOf(
                    "org.jetbrains.kotlin.jvm",
                    "org.jetbrains.kotlin.multiplatform",
                    "org.jetbrains.kotlin.plugin.serialization")
            ) {
                val kotlinVersion: String by settings
                useVersion(kotlinVersion)
            }
        }
    }
}
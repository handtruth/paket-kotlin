pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    val kotlinVersion: String by settings
    val gitAndroidVersion: String by settings
    val gradlePublishPlugin: String by settings
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.jetbrains.kotlin"))
                useVersion(kotlinVersion)
        }
    }
    plugins {
        id("com.gladed.androidgitversion") version gitAndroidVersion
        id("com.gradle.plugin-publish") version gradlePublishPlugin
    }
}

rootProject.name = "paket-kotlin"

enableFeaturePreview("GRADLE_METADATA")

fun module(name: String) {
    include(":$name")
    project(":$name").projectDir = file("modules/$name")
}

module("paket-kotlin-plugin")
module("paket-gradle")

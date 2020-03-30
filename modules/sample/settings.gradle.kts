pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://mvn.handtruth.com")
    }
    val kotlinVersion: String by settings
    val paketVersion: String by settings
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.jetbrains.kotlin"))
                useVersion(kotlinVersion)
            if (requested.id.id == "com.handtruth.mc.paket")
                useModule("com.handtruth.mc:paket-gradle:$paketVersion")
        }
    }
}

rootProject.name = "sample"

enableFeaturePreview("GRADLE_METADATA")

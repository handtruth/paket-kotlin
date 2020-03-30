@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("com.gladed.androidgitversion")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
    jacoco
}

androidGitVersion {
    prefix = "v"
}

group = "com.handtruth.mc"
version = androidGitVersion.name()

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("http://maven.handtruth.com/")
        }
    }
}

kotlin {
    jvm()
    js {
        browser()
        nodejs()
    }
    sourceSets {
        fun String.suffix(str: String) = if (str.isEmpty()) this else "$this-$str"
        fun kotlinx(name: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$name:$version"
        fun io(name: String = "") = kotlinx("io".suffix(name), "0.2.0")
        fun coroutines(name: String = "") = kotlinx("coroutines".suffix(name), "1.3.5")
        fun serializationRuntime(name: String = "") = kotlinx("serialization-runtime".suffix(name), "0.20.0")
        fun mc(name: String, version: String) = "com.handtruth.mc:$name:$version"
        all {
            with (languageSettings) {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
                useExperimentalAnnotation("com.handtruth.mc.paket.ExperimentalPaketApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(io())
                implementation(coroutines("core-common"))
                implementation(serializationRuntime("common"))
                compileOnly(mc("nbt-kotlin", "0.1.0"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(coroutines("core"))
                implementation(serializationRuntime())
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(coroutines("test"))
                implementation(kotlin("test-junit"))
                implementation(mc("nbt-kotlin", "0.1.0"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(coroutines("core-js"))
                implementation(serializationRuntime("js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.5"
    reportsDir = file("${buildDir}/jacoco-reports")
}

tasks {
    val jvmTest by getting
    val testCoverageReport by creating(JacocoReport::class) {
        dependsOn(jvmTest)
        group = "Reporting"
        description = "Generate Jacoco coverage reports."
        val coverageSourceDirs = arrayOf(
                "commonMain/src",
                "jvmMain/src"
        )
        val classFiles = file("${buildDir}/classes/kotlin/jvm/")
                .walkBottomUp()
                .toSet()
        classDirectories.setFrom(classFiles)
        sourceDirectories.setFrom(files(coverageSourceDirs))
        additionalSourceDirs.setFrom(files(coverageSourceDirs))

        executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.isEnabled = true
            html.destination = file("${buildDir}/jacoco-reports/html")
        }
    }
    val prepareSample by creating(WriteProperties::class) {
        outputFile = file("modules/sample/gradle.properties")
        comment = "Auto generated file"
        val kotlinVersion: String by project
        val paketVersion = project.version
        properties(
            "kotlinVersion" to kotlinVersion,
            "paketVersion" to paketVersion
        )
    }
    val publishToMavenLocal by getting {
        dependsOn(prepareSample)
    }
}

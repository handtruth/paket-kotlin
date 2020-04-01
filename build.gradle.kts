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
        maven("http://maven.handtruth.com/")
    }
    val platformVersion: String by project
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.handtruth.internal" && requested.name == "platform")
                useVersion(platformVersion)
        }
    }
}

kotlin {
    jvm()
    js {
        browser {
            testTask {
                useKarma {
                    usePhantomJS()
                }
            }
        }
        nodejs()
    }
    sourceSets {
        fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
        fun mc(name: String) = "$group:$name"
        all {
            with (languageSettings) {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
                useExperimentalAnnotation("com.handtruth.mc.paket.ExperimentalPaketApi")
            }
            dependencies {
                val platform = dependencies.platform("com.handtruth.internal:platform")
                implementation(platform)
                compileOnly(platform)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlinx("io"))
                implementation(kotlinx("coroutines-core-common"))
                implementation(kotlinx("serialization-runtime-common"))
                compileOnly(mc("nbt-kotlin"))
                compileOnly("io.ktor:ktor-io")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(mc("nbt-kotlin"))
                implementation("io.ktor:ktor-io")
                implementation("io.ktor:ktor-test-dispatcher")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlinx("coroutines-core"))
                implementation(kotlinx("serialization-runtime"))
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                compileOnly("io.ktor:ktor-io-jvm")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.ktor:ktor-io-jvm")
                implementation("io.ktor:ktor-test-dispatcher-jvm")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(kotlinx("coroutines-core-js"))
                implementation(kotlinx("serialization-runtime-js"))
                compileOnly("io.ktor:ktor-io-js")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.ktor:ktor-io-js")
                implementation("io.ktor:ktor-test-dispatcher-js")
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

@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("com.gladed.androidgitversion")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    `maven-publish`
    jacoco
}

androidGitVersion {
    prefix = "v"
}

group = "com.handtruth.mc"
version = androidGitVersion.name()

val platformVersion: String by project

atomicfu {
    //dependenciesVersion = null
}

allprojects {
    repositories {
        jcenter()
        maven("https://mvn.handtruth.com/")
        maven("https://dl.bintray.com/korlibs/korlibs")
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.handtruth.internal" && requested.name == "platform")
                useVersion(platformVersion)
        }
    }
}

kotlin {
    jvm()
    val useJS: String by project
    val useJSBool = useJS == "true"
    if (useJSBool)
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
        fun kommon(name: String) = "com.handtruth.kommon:kommon-$name"
        fun mc(name: String) = "$group:$name"
        all {
            with (languageSettings) {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
                useExperimentalAnnotation("com.handtruth.mc.paket.ExperimentalPaketApi")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            }
            dependencies {
                val platform = dependencies.platform("com.handtruth.internal:platform:$platformVersion")
                implementation(platform)
                compileOnly(platform)
                api(platform)
            }
        }
        val atomicfuVersion: String by project
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib"))
                api(kotlinx("io"))
                implementation(kotlinx("coroutines-core-common"))
                implementation(kotlinx("serialization-runtime-common"))
                //implementation(kommon("concurrent"))
                //implementation("org.jetbrains.kotlinx:atomicfu-common:$atomicfuVersion")
                compileOnly(mc("nbt-kotlin"))
                compileOnly("io.ktor:ktor-io")
                compileOnly("com.soywiz.korlibs.korio:korio")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(mc("nbt-kotlin"))
                implementation("io.ktor:ktor-io")
                implementation("com.soywiz.korlibs.korio:korio")
                implementation("io.ktor:ktor-test-dispatcher")
            }
        }
        val jvmMain by getting {
            dependencies {
                api(kotlinx("coroutines-core"))
                implementation(kotlinx("serialization-runtime"))
                api(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                //implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
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
        if(useJSBool) {
            val jsMain by getting {
                dependencies {
                    api(kotlin("stdlib-js"))
                    api(kotlinx("coroutines-core-js"))
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
}

jacoco {
    toolVersion = "0.8.5"
    reportsDir = file("${buildDir}/jacoco-reports")
}

tasks {
    val jvmTest by getting {}
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

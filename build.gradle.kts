import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    jacoco
}

group = "com.handtruth.mc"
version = "3.1.0"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("http://maven.handtruth.com/")
    }
}

dependencies {
    implementation(platform("com.handtruth.internal:platform:0.0.1"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    implementation(kotlinx("io-jvm"))
    implementation(kotlinx("coroutines-core"))
    implementation(kotlinx("serialization-runtime"))

    fun mc(name: String) = "com.handtruth.mc:$name"
    implementation(mc("nbt-kotlin"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlinx("coroutines-test"))
    testImplementation(kotlin("test"))
}

jacoco {
    toolVersion = "0.8.5"
    reportsDir = file("$buildDir/customJacocoReportDir")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
                "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer",
                "-XXLanguage:+InlineClasses"
            )
        }
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    jacocoTestReport {
        reports {
            xml.isEnabled = false
            csv.isEnabled = false
            html.destination = file("$buildDir/jacocoHtml")
        }
    }
}

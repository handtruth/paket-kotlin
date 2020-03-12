import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    jacoco
}

group = "com.handtruth.mc"
version = "2.1.0"

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenLocal()
    }
}

dependencies {
    implementation(platform(project(":platform")))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    implementation(kotlinx("io-jvm"))
    implementation(kotlinx("coroutines-core"))

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
            //verbose = true
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
                "-Xuse-experimental=kotlin.contracts.ExperimentalContracts",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
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

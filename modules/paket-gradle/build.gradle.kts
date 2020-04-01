import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish")
    kotlin("jvm")
    kotlin("kapt")
    jacoco
}

group = rootProject.group
version = rootProject.version

val pluginId = "com.handtruth.mc.paket"

dependencies {
    val handtruthPlatform = platform("com.handtruth.internal:platform")
    implementation(handtruthPlatform)
    kapt(handtruthPlatform)

    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))

    kapt("com.google.auto.service:auto-service")
    compileOnly("com.google.auto.service:auto-service-annotations")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

gradlePlugin {
    plugins {
        create("KotlinPaketPlugin") {
            id = pluginId
            displayName = "Kotlin Plugin for Paket Library"
            description = "Kotlin compiler plugin for automatic generation companions for paket classes"
            implementationClass = "com.handtruth.mc.paket.compiler.PaketCompilerGradlePlugin"
        }
    }
}

pluginBundle {
    website = "http://mc.handtruth.com/"
    vcsUrl = "https://github.com/handtruth/paket-kotlin.git"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = project.name
        }
    }
}

jacoco {
    toolVersion = "0.8.5"
    reportsDir = file("$buildDir/customJacocoReportDir")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
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
    val paketProperties by creating(WriteProperties::class) {
        outputFile = file("$buildDir/resources/main/META-INF/paket.properties")
        comment = "Paket Gradle Plugin Properties"
        properties(
            "group" to "com.handtruth.mc",
            "version" to version,
            "artifactId" to project.name,
            "pluginId" to pluginId,
            "kotlinPluginArtifactId" to "paket-kotlin-plugin",
            "paketRuntime" to "paket-kotlin"
        )
    }
    getByName("processResources") {
        dependsOn(paketProperties)
    }
}

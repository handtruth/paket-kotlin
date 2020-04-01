plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

dependencies {
    val handtruthPlatform = platform("com.handtruth.internal:platform")
    implementation(handtruthPlatform)
    kapt(handtruthPlatform)

    implementation(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("compiler-embeddable"))

    kapt("com.google.auto.service:auto-service")
    compileOnly("com.google.auto.service:auto-service-annotations")

    testImplementation(kotlin("test-junit"))
    //testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    //testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.6")
}

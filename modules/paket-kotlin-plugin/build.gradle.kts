plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

dependencies {
    val platformVersion: String by project
    implementation(platform("com.handtruth.internal:platform:$platformVersion"))
    kapt(platform("com.handtruth.internal:platform:$platformVersion"))

    implementation(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("compiler-embeddable"))

    kapt("com.google.auto.service:auto-service")
    compileOnly("com.google.auto.service:auto-service-annotations")

    testImplementation(kotlin("test-junit"))
    //testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    //testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.6")
}

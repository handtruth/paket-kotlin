plugins {
    id("com.handtruth.mc.paket")
    kotlin("jvm")
}

paket {

}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:paket-kotlin")
}

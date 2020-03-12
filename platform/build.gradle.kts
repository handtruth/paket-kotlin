plugins {
    base
}

dependencies {
    constraints {
        default("org.junit.jupiter:junit-jupiter:5.5.2")
        fun kotlinx(name: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$name:$version"
        default(kotlinx("io-jvm", "0.2.0"))
        default(kotlinx("coroutines-core", "1.3.4"))
        default(kotlinx("coroutines-test", "1.3.4"))
        default(kotlinx("io", "0.2.0"))
    }
}

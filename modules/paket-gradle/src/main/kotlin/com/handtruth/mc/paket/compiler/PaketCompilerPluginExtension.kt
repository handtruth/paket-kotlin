package com.handtruth.mc.paket.compiler

enum class PaketSources {
    Pool, Creator
}

open class PaketCompilerPluginExtension {
    var source: PaketSources = PaketSources.Creator
    val runtime = PaketProperties.paketRuntime
}

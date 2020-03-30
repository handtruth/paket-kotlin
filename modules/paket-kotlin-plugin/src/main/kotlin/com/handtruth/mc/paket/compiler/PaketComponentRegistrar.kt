package com.handtruth.mc.paket.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

val KEY_PAKET_SOURCE = CompilerConfigurationKey<PaketSources>("paket object generator")

@AutoService(ComponentRegistrar::class)
class PaketComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        TODO("Not yet implemented")
    }
}

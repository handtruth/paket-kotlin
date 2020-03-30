package com.handtruth.mc.paket.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class PaketCompilerGenerationExtension(private val source: PaketSources) : IrGenerationExtension {
    constructor(source: String) : this(PaketSources.valueOf(source))

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        TODO("Not yet implemented")
    }
}

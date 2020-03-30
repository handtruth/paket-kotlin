package com.handtruth.mc.paket.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(CommandLineProcessor::class)
class PaketCompilerCommandLineProcessor : CommandLineProcessor {
    override val pluginId = "com.handtruth.mc.paket"
    override val pluginOptions: Collection<CliOption>
        get() = listOf(
            CliOption(
                "source",
                "<creator|pool>",
                "paket object generator type",
                allowMultipleOccurrences = false,
                required = false
        ))

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            "source" -> {
                val source = when (val it = value.toLowerCase()) {
                    "creator", "c" -> PaketSources.Creator
                    "pool", "p" -> PaketSources.Pool
                    else -> error("unknown paket source: $it")
                }
                configuration.put(KEY_PAKET_SOURCE, source)
            }
            else -> error("Unknown CLI option: ${option.optionName}")
        }
    }
}

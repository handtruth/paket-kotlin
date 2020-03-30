package com.handtruth.mc.paket.compiler

import org.gradle.api.Plugin
import org.gradle.api.Project

class PaketCompilerGradlePlugin : Plugin<Project> {

    inline val pp get() = PaketProperties

    override fun apply(project: Project): Unit = with(project) {
        pluginManager.apply("base")
        extensions.create("paket", PaketCompilerPluginExtension::class.java)
        configurations.all { config ->
            config.resolutionStrategy.eachDependency {
                val rd = it.requested
                if (rd.group == pp.group && rd.name == pp.paketRuntime
                        && (rd.version.isNullOrEmpty() || rd.version == "default")) {
                    it.useVersion(pp.version)
                }
            }
        }
    }
}

package com.handtruth.mc.paket.compiler

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@AutoService(KotlinGradleSubplugin::class)
class PaketCompilerGradleSubPlugin : KotlinGradleSubplugin<AbstractCompile> {
    override fun getCompilerPluginId() = PaketProperties.pluginId

    override fun isApplicable(project: Project, task: AbstractCompile) =
        project.plugins.hasPlugin(PaketCompilerGradlePlugin::class.java)

    override fun getPluginArtifact() = SubpluginArtifact(
        groupId = PaketProperties.group,
        artifactId = PaketProperties.kotlinPluginArtifactId,
        version = PaketProperties.version
    )

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val extension = project.extensions.findByType(PaketCompilerPluginExtension::class.java)
            ?: PaketCompilerPluginExtension()
        return listOf(SubpluginOption("source", extension.source.toString()))
    }
}

package com.handtruth.mc.paket.compiler

import java.util.*
import kotlin.reflect.KProperty

object PaketProperties {
    private val properties = Properties()

    init {
        val resource = javaClass.classLoader.getResource("META-INF/paket.properties")
        properties.load(resource!!.openStream()!!)
    }

    private operator fun getValue(thisRef: Any, property: KProperty<*>) =
        properties[property.name] as String

    val group by this
    val version by this
    val artifactId by this
    val pluginId by this
    val kotlinPluginArtifactId by this
    val paketRuntime by this

    val runtimeCoordinates by lazy {
        "$group:$paketRuntime:$version"
    }
}

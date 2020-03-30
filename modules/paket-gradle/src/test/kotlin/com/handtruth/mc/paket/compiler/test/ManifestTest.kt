package com.handtruth.mc.paket.compiler.test

import com.handtruth.mc.paket.compiler.PaketProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ManifestTest {
    @Test
    fun loadManifest() {
        PaketProperties
        assertEquals("com.handtruth.mc", PaketProperties.group)
        println(PaketProperties.version)
        assertEquals("paket-kotlin", PaketProperties.paketRuntime)
    }
}

package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.receive
import com.soywiz.korio.stream.MemoryAsyncStreamBase
import com.soywiz.korio.stream.toAsyncStream
import io.ktor.test.dispatcher.testSuspend
import kotlin.test.*

class KorioStreamsTest {

    @Test
    fun korioStreamTest() = testSuspend {
        val paketA = ProtocolTest.paket
        val stream = MemoryAsyncStreamBase()
        val ts = PaketTransmitter(stream.toAsyncStream(), stream.toAsyncStream())
        ts.send(paketA)
        val paketB = ts.receive(ProtocolTest.ExamplePaket)
        assertEquals(paketA, paketB)
        ts.send(paketA)
        assertFalse(ts.isCaught)
        assertEquals(paketA.id.ordinal, ts.catchOrdinal())
        assertTrue(ts.isCaught)
        ts.drop()
        assertFalse(ts.isCaught)
        assertFails {
            ts.catchOrdinal()
        }
    }

}

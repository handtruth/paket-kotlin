package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.peek
import com.handtruth.mc.paket.receive
import com.soywiz.korio.stream.MemoryAsyncStreamBase
import com.soywiz.korio.stream.toAsyncStream
import io.ktor.test.dispatcher.testSuspend
import io.ktor.utils.io.ByteChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.io.Bytes
import kotlin.test.*

class TransmittersTest {

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

    @Test
    fun channeledTest() = testSuspend {
        val channel = ByteChannel()
        val ts = PaketTransmitter(channel)
        val paketA = ProtocolTest.paket
        val task = async {
            ts.send(paketA)
            println("Data sent")
        }
        println("Task spawned")
        val paketB = ts.receive(ProtocolTest.ExamplePaket)
        println("Paket received")
        task.await()
        println("Task finished")
        assertEquals(paketA, paketB)

        ts.send(paketA)
        ts.drop()
        assertEquals(0, channel.availableForRead)
    }

    @Test
    fun bytesChannelsTest() = testSuspend {
        val channel = Channel<Bytes>(Channel.UNLIMITED)
        val ts = PaketTransmitter(channel)
        val paketA = ProtocolTest.paket
        ts.send(paketA)
        println("Data sent")
        val peeked = ts.peek(ProtocolTest.ExamplePaket)
        println("Peeked")
        val paketB = ts.receive(ProtocolTest.ExamplePaket)
        println("Paket received")
        assertEquals(paketA, peeked)
        assertEquals(paketA, paketB)

        ts.send(paketA)
        ts.drop()
    }

    @Test
    fun paketChannelsTest() = testSuspend {
        val channel = Channel<Paket>(Channel.UNLIMITED)
        val ts = PaketTransmitter(channel)
        val paketA = ProtocolTest.paket
        ts.send(paketA)
        println("Data sent")
        val peeked = ts.peek(ProtocolTest.ExamplePaket)
        println("Peeked")
        val paketB = ts.receive(ProtocolTest.ExamplePaket)
        println("Paket received")
        assertEquals(paketA, peeked)
        assertEquals(paketA, paketB)

        ts.send(paketA)
        ts.drop()
    }

}
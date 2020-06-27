package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.enum
import com.handtruth.mc.paket.fields.int32
import com.handtruth.mc.paket.fields.string
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
        val peeked = ts.peek(ProtocolTest.ExamplePaket)
        val paketB = ts.receive(ProtocolTest.ExamplePaket)
        assertEquals(paketA, paketB)
        assertEquals(paketA, peeked)
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
        val peeked = ts.peek(ProtocolTest.ExamplePaket)
        val paketB = ts.receive(ProtocolTest.ExamplePaket)
        println("Paket received")
        task.await()
        println("Task finished")
        assertEquals(paketA, paketB)
        assertEquals(paketA, peeked)

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

    enum class SomeIDs {
        One, Two, Three
    }

    open class BasePaket(type: NextTypes) : Paket() {
        override val id = SomeIDs.Two
        val type by enum(type)

        enum class NextTypes {
            First, Second
        }

        companion object : PaketCreator<BasePaket> {
            override fun produce() = BasePaket(NextTypes.First)
        }
    }

    class FirstPaket(str: String = "lolkekdude") : BasePaket(NextTypes.First) {
        var str by string(str)

        companion object : PaketCreator<FirstPaket> {
            override fun produce() = FirstPaket()
        }
    }

    class SecondPaket(number: Int = 0) : BasePaket(NextTypes.Second) {
        var number by int32(number)

        companion object : PaketCreator<SecondPaket> {
            override fun produce() = SecondPaket()
        }
    }

    @Test
    fun protocolInheritanceTest() = testSuspend {
        val channel = ByteChannel()
        val ts = PaketTransmitter(channel)
        val paketA = FirstPaket("ВНИМАНИЕ".repeat(100))
        val task = async {
            ts.send(paketA)
        }
        val peeked = ts.peek(BasePaket)
        assertEquals(BasePaket.NextTypes.First, peeked.type)
        val paketB = ts.receive(FirstPaket)
        task.await()
        assertEquals(paketA, paketB)

        ts.send(paketA)
        ts.drop()
        assertEquals(0, channel.availableForRead)
    }

}

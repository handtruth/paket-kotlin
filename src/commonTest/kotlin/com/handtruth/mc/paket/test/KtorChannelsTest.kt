package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketCreator
import com.handtruth.mc.paket.PaketTransmitter
import com.handtruth.mc.paket.fields.json
import com.handtruth.mc.paket.receive
import io.ktor.test.dispatcher.testSuspend
import io.ktor.utils.io.ByteChannel
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorChannelsTest {

    enum class ExampleID {
        One
    }

    @Serializable
    data class SomeData(val string: String = "", val long: Long = 0)

    class ExamplePaket(data: SomeData = SomeData()) : Paket() {
        override val id = ExampleID.One
        val data by json(data)

        companion object : PaketCreator<ExamplePaket> {
            override fun produce() = ExamplePaket()
        }
    }

    @Test
    fun channeledTest() = testSuspend {
        val channel = ByteChannel()
        val ts = PaketTransmitter(channel)
        val paketA = ExamplePaket(SomeData("Lol Kek Kotlin bergh", 484239624385365))
        val task = async {
            ts.send(paketA)
            println("Data sent")
        }
        println("Task spawned")
        val paketB = ts.receive(ExamplePaket)
        println("Paket received")
        task.await()
        println("Task finished")
        assertEquals(paketA, paketB)

        ts.send(paketA)
        ts.drop()
        assertEquals(0, channel.availableForRead)
    }
}

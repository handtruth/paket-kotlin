package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.paket
import com.handtruth.mc.paket.fields.string
import com.handtruth.mc.paket.fields.varInt
import io.ktor.test.dispatcher.testSuspend
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.io.Bytes
import kotlin.test.Test
import kotlin.test.assertEquals

class NestTest {

    enum class IDS {
        First, Second, Third
    }

    open class HeadPaket : Paket() {
        override val id = IDS.Second

        val seq by varInt(counter++)

        companion object {
            private var counter = 0
        }
    }

    class BodyPaket(body: Paket) : HeadPaket() {
        val body by paket(body)
    }

    object MyNest : NestSource<HeadPaket> {
        override fun head() = HeadPaket()
        override fun produce(paket: Paket) = BodyPaket(paket)
    }

    enum class IDS2 {
        First, Second, Third
    }

    object FirstPaket : SinglePaket<FirstPaket>() {
        override val id = IDS2.First
    }

    object SecondPaket : SinglePaket<SecondPaket>() {
        override val id = IDS2.Second

        val str by string("kotlinbergh")
    }

    object ThirdPaket : SinglePaket<ThirdPaket>() {
        override val id = IDS2.Third
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun nestTest() = testSuspend {
        val main = PaketTransmitter(Channel<Bytes>())
        val ts = main nest MyNest
        val n = 1000
        coroutineScope {
            launch(start = CoroutineStart.UNDISPATCHED) {
                main.catchOrdinal()
            }
            launch {
                ts.send(FirstPaket)
            }

        }
        coroutineScope {
            launch {
                repeat(n) {
                    ts.send(FirstPaket)
                    ts.send(ThirdPaket)
                    ts.send(SecondPaket)
                }
            }
            launch {
                repeat(n * 3 + 1) {
                    when (ts.catch<IDS2>()) {
                        IDS2.First -> ts.receive(FirstPaket)
                        IDS2.Second -> {
                            assertEquals(SecondPaket.size, ts.size)
                            ts.receive(SecondPaket)
                            assertEquals("kotlinbergh", SecondPaket.str)
                        }
                        IDS2.Third -> {
                            ts.peek(ThirdPaket)
                            ts.receive(ThirdPaket)
                        }
                    }
                }
            }
        }
    }

}

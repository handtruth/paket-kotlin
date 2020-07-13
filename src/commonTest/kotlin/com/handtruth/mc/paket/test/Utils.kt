package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import io.ktor.test.dispatcher.testSuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.io.use
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

internal suspend fun <P : Paket> writeReadPaket(paketA: P, source: PaketSource<P>): P {
    // write
    val output = ByteArrayOutput()
    val sender = PaketSender(output)
    assertFalse(sender.broken, "Sender was broken after creation")
    val bytes = sender.use {
        it.send(paketA)
        output.toByteArray()
    }
    assertTrue(sender.broken, "Sender was not broken after close operation")
    assertFailsWith<BrokenObjectException>("Useful broken senders should not operate") {
        sender.send(paketA)
    }
    val size = paketA.size
    run {
        val expected = size + sizeVarInt(size)
        assertEquals(expected, bytes.size, "Written and expected sizes differs ($expected expected, got ${bytes.size})")
    }
    // read
    val input = ByteArrayInput(bytes)
    val receiver = PaketReceiver(input)
    assertFalse(receiver.broken, "Receiver was broken after creation")
    assertFalse(receiver.isCaught, "Paket receiver should be empty at the beginning")
    assertEquals(paketA.id.ordinal, receiver.catchOrdinal(), "Receiver got wrong paket id")
    assertEquals(paketA.id.ordinal, receiver.idOrdinal, "Receiver contains wrong paket id")
    assertEquals(size, receiver.size, "Receiver got wrong paket size")
    assertTrue(receiver.isCaught, "Paket receiver should caught a paket")
    val paketB: P = receiver.use {
        val r = it.receive(source)
        assertFalse(it.isCaught, "Paket receiver should be empty after paket paket reading operation")
        r
    }
    assertTrue(receiver.broken, "Receiver was not broken after close operation")
    assertFailsWith<BrokenObjectException>("Useful broken receivers should not operate") {
        receiver.catchOrdinal()
    }
    // final comparision
    assertEquals(paketA, paketB)
    assertEquals(paketA.toString(), paketB.toString())
    assertEquals(paketA.hashCode(), paketB.hashCode())
    return paketB
}

fun testTimeout(duration: Duration, block: suspend CoroutineScope.() -> Unit) = testSuspend {
    try {
        withTimeout(duration, block)
    } catch (e: TimeoutCancellationException) {
        throw IllegalStateException("timeout", e)
    }
}

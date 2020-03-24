@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Input
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

interface PaketReceiver : Breakable {
    val idOrdinal: Int
    val size: Int
    val isCaught: Boolean
    suspend fun catchOrdinal(): Int
    suspend fun drop()
    suspend fun receive(paket: Paket)
    suspend fun peek(paket: Paket)
}

suspend fun <P: Paket> PaketReceiver.receive(`class`: KClass<out P>): P {
    val paket = constructPaket(`class`)
    receive(paket)
    return paket
}

suspend inline fun <reified P: Paket> PaketReceiver.receive() = receive(P::class)

suspend fun <P: Paket> PaketReceiver.peek(`class`: KClass<out P>): P {
    val paket = constructPaket(`class`)
    peek(paket)
    return paket
}

suspend inline fun <reified P: Paket> PaketReceiver.peek() = peek(P::class)

fun PaketReceiver(input: Input, ioContext: CoroutineContext = Dispatchers.IO): PaketReceiver =
    InputPaketReceiver(input, ioContext)

abstract class AbstractPaketReceiver : AbstractBreakable(), PaketReceiver {
    override var idOrdinal = -1
        protected set
    override var size = -1
        protected set
    override var isCaught = false
        protected set
}

private class InputPaketReceiver(val channel: Input, private val ioContext: CoroutineContext) :
        AbstractPaketReceiver() {

    override suspend fun catchOrdinal(): Int = breakableAction {
        if (isCaught) {
            drop()
            catchOrdinal()
        } else withContext(ioContext) {
            size = readVarInt(channel)
            val id = channel.preview {
                readVarInt(this)
            }
            isCaught = true
            idOrdinal = id
            id
        }
    }

    override suspend fun drop(): Unit = breakableAction {
        if (isCaught) {
            val size = size
            val skipped = withContext(ioContext) { channel.discard(size) }
            validate(skipped == size) {
                "Input ended, but paket not dropped correctly ($skipped bytes dropped of $size)"
            }
            isCaught = false
        } else {
            catchOrdinal()
            drop()
        }
    }

    override suspend fun receive(paket: Paket) = breakableAction {
        if (!isCaught)
            catchOrdinal()
        val id = idOrdinal
        validate(id == paket.id.ordinal) { "Paket IDs differ (${paket.id.ordinal} expected, got $id)" }
        withContext(ioContext) {
            paket.read(channel)
            val estimate = size - paket.size
            val skipped = channel.discard(estimate)
            validate(estimate == skipped) {
                "Failed to discard paket estimate ($estimate estimated, skipped $skipped)"
            }
        }
        isCaught = false
    }

    override suspend fun peek(paket: Paket) {
        TODO("Not yet implemented")
    }

    override fun close() {
        super.close()
        channel.close()
    }
}

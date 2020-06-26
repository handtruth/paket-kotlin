@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.withContext
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

private class OutputPaketSender(
    val channel: Output,
    private val ioContext: CoroutineContext?
) : AbstractPaketSender() {
    override suspend fun send(paket: Paket) = breakableAction {
        val size = paket.size
        withContext(ioContext ?: coroutineContext) {
            writeVarInt(channel, size)
            paket.write(channel)
        }
    }

    override fun close() {
        super.close()
        channel.close()
    }
}

private class InputPaketReceiver(val channel: Input, private val ioContext: CoroutineContext?) :
    AbstractPaketReceiver() {

    override suspend fun catchOrdinal(): Int = breakableAction {
        if (isCaught) {
            drop()
            catchOrdinal()
        } else withContext(ioContext ?: coroutineContext) {
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
            val skipped = withContext(ioContext ?: coroutineContext) { channel.discard(size) }
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
        withContext(ioContext ?: coroutineContext) {
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
        if (!isCaught)
            catchOrdinal()
        withContext(ioContext ?: coroutineContext) {
            channel.preview {
                paket.read(channel)
            }
        }
    }

    override fun close() {
        super.close()
        channel.close()
    }
}

fun PaketSender(output: Output, ioContext: CoroutineContext? = null): PaketSender =
    OutputPaketSender(output, ioContext)

fun PaketReceiver(input: Input, ioContext: CoroutineContext? = null): PaketReceiver =
    InputPaketReceiver(input, ioContext)

fun PaketTransmitter(input: Input, output: Output, ioContext: CoroutineContext? = null) =
    PaketTransmitter(PaketReceiver(input, ioContext), PaketSender(output, ioContext))

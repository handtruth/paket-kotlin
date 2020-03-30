@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.ByteArrayOutput
import kotlinx.io.Output
import kotlin.coroutines.CoroutineContext

interface PaketSender : Breakable {
    suspend fun send(paket: Paket)
}

fun PaketSender(output: Output, ioContext: CoroutineContext): PaketSender =
    OutputPaketSender(output, ioContext)
fun PaketSender(channel: SendChannel<ByteArray>): PaketSender = ByteArrayPaketSender(channel)

abstract class AbstractPaketSender : AbstractBreakable(), PaketSender {
    protected fun prepareByteArray(paket: Paket): ByteArray {
        val size = paket.size
        val sizeOfSize = sizeVarInt(size)
        val output = ByteArrayOutput(size + sizeOfSize)
        writeVarInt(output, size)
        paket.write(output)
        val bytes = output.toByteArray()
        validate(bytes.size - sizeOfSize == size) {
            "Paket produced wrong amount of data ($size expected, got ${bytes.size - sizeOfSize})"
        }
        return bytes
    }
}

private class OutputPaketSender(
    val channel: Output,
    private val ioContext: CoroutineContext
) : AbstractPaketSender() {
    override suspend fun send(paket: Paket) = breakableAction {
        val size = paket.size
        withContext(ioContext) {
            writeVarInt(channel, size)
            paket.write(channel)
        }
    }

    override fun close() {
        super.close()
        channel.close()
    }
}

private class ByteArrayPaketSender(private val output: SendChannel<ByteArray>) :
        AbstractPaketSender() {
    override suspend fun send(paket: Paket) = breakableAction {
        output.send(prepareByteArray(paket))
    }

    override fun close() {
        super.close()
        output.close(RuntimeException("Closed"))
    }
}

fun PaketSender.asSynchronized(): PaketSender = SynchronizedPaketSender(this)

private class SynchronizedPaketSender(private val sender: PaketSender) : PaketSender {

    private val mutex = Mutex()

    override val broken get() = sender.broken
    override suspend fun send(paket: Paket) = mutex.withLock { sender.send(paket) }

    override fun close() = sender.close()
}

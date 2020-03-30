@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlinx.io.Input
import kotlin.coroutines.CoroutineContext

interface PaketReceiver : Breakable {
    val idOrdinal: Int
    val size: Int
    val isCaught: Boolean
    suspend fun catchOrdinal(): Int
    suspend fun drop()
    suspend fun receive(paket: Paket)
    suspend fun peek(paket: Paket)
}

suspend fun <P: Paket> PaketReceiver.receive(source: PaketSource<P>) = source.produce().also { receive(it) }

suspend fun <P: Paket> PaketReceiver.peek(source: PaketSource<P>) = source.produce().also { peek(it) }

fun PaketReceiver(input: Input, ioContext: CoroutineContext): PaketReceiver = InputPaketReceiver(input, ioContext)

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

fun PaketReceiver.asSynchronized(): PaketReceiver = SynchronizedPaketReceiver(this)

private class SynchronizedPaketReceiver(private val receiver: PaketReceiver) : PaketReceiver {

    private val mutex = Mutex()

    override val idOrdinal get() = receiver.idOrdinal
    override val size get() = receiver.size
    override val isCaught get() = receiver.isCaught
    override val broken get() = receiver.broken

    private inline fun <R> release(block: () -> R) =
        try { block() } finally { mutex.unlock() }

    override suspend fun catchOrdinal(): Int {
        mutex.lock()
        return receiver.catchOrdinal()
    }
    override suspend fun drop() = release { receiver.drop() }
    override suspend fun receive(paket: Paket) = release { receiver.drop() }
    override suspend fun peek(paket: Paket) = receiver.peek(paket)
    override fun close() = receiver.close()
}

inline fun <reified E: Enum<E>> PaketReceiver.getId() = enumValues<E>()[idOrdinal]

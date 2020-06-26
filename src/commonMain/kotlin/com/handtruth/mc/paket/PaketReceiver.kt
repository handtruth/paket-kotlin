@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.sync.Mutex

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

abstract class AbstractPaketReceiver : AbstractBreakable(), PaketReceiver {
    override var idOrdinal = -1
        protected set
    override var size = -1
        protected set
    override var isCaught = false
        protected set
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

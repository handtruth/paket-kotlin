@file:Suppress("FunctionName")

package com.handtruth.mc.paket

interface PaketPeeking {
    val idOrdinal: Int
    val size: Int
    suspend fun peek(paket: Paket)
}

interface PaketReceiver : PaketPeeking, Breakable {
    val isCaught: Boolean
    suspend fun catchOrdinal(): Int
    suspend fun drop()
    suspend fun receive(paket: Paket)
}

suspend fun <P : Paket> PaketReceiver.receive(source: PaketSource<P>) = source.produce().also { receive(it) }

suspend fun <P : Paket> PaketPeeking.peek(source: PaketSource<P>) = source.produce().also { peek(it) }

abstract class AbstractPaketReceiver : AbstractBreakable(), PaketReceiver {
    override var idOrdinal = -1
        get() = breakableAction { field }
        protected set
    override var size = -1
        get() = breakableAction { field }
        protected set
    override var isCaught = false
        get() = breakableAction { field }
        protected set
}

inline fun <reified E : Enum<E>> PaketPeeking.getId() = enumValues<E>()[idOrdinal]
suspend inline fun <reified E : Enum<E>> PaketReceiver.catch() = enumValues<E>()[catchOrdinal()]

suspend inline fun PaketReceiver.dropAll(): Nothing {
    while (true) drop()
}

fun PaketReceiver.asNotCloseable(): PaketReceiver = object : PaketReceiver by this {
    override fun close() {}
}

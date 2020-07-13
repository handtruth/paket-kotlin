package com.handtruth.mc.paket

inline infix fun PaketReceiver.filter(crossinline block: (PaketPeeking) -> Boolean): PaketReceiver {
    return object : PaketFilter(this) {
        override fun validate(peeking: PaketPeeking) = block(peeking)
    }
}

inline infix fun PaketTransmitter.filter(crossinline block: (PaketPeeking) -> Boolean): PaketTransmitter {
    val rx: PaketReceiver = this
    return PaketTransmitter(rx filter block, this)
}

@PublishedApi
internal abstract class PaketFilter(private val parent: PaketReceiver) : PaketReceiver by parent {

    abstract fun validate(peeking: PaketPeeking): Boolean

    override suspend fun catchOrdinal(): Int {
        var result: Int
        do {
            result = parent.catchOrdinal()
        } while (!validate(parent))
        return result
    }

    override suspend fun drop() {
        if (!isCaught) {
            catchOrdinal()
            drop()
        } else {
            parent.drop()
        }
    }

    override suspend fun receive(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        parent.receive(paket)
    }

    override suspend fun peek(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        parent.peek(paket)
    }
}

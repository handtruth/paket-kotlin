package com.handtruth.mc.paket

import com.handtruth.mc.paket.fields.varInt

interface NestPaketTransmitter : PaketTransmitter {
    val headerSize: Int
    val fullSize: Int
}

interface NestSource<N : Paket> {
    fun produce(paket: Paket): N
    fun head(): N
}

infix fun <N : Paket> PaketTransmitter.nest(source: NestSource<N>): NestPaketTransmitter =
    NestPaketTransmitterImpl(this, source)

private class NestPaketTransmitterImpl(
    private val ts: PaketTransmitter,
    private val source: NestSource<*>
) : NestPaketTransmitter {

    override val broken get() = ts.broken

    private val head = source.head()

    private val idField = head.varInt(-1)

    override var isCaught = false

    override var idOrdinal by idField
    override var size = -1
    override var headerSize = -1
    override val fullSize get() = size + headerSize

    override suspend fun catchOrdinal(): Int {
        if (!ts.isCaught)
            ts.catchOrdinal()
        isCaught = true
        ts.peek(head)
        headerSize = head.size - idField.size
        size = ts.size - headerSize
        return idOrdinal
    }

    private fun clear() {
        isCaught = false
        idOrdinal = -1
        size = -1
        headerSize = -1
    }

    override suspend fun drop() {
        ts.drop()
        clear()
    }

    override suspend fun receive(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        val nest = source.produce(paket)
        ts.receive(nest)
        clear()
    }

    override suspend fun peek(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        val nest = source.produce(paket)
        ts.peek(nest)
    }

    override suspend fun send(paket: Paket) {
        val nest = source.produce(paket)
        ts.send(nest)
    }

    override fun close() {
        clear()
        ts.close()
    }
}

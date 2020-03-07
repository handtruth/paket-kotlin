package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class VarLongField(paket: Paket, initial: Long) : Field<Long>(paket, initial) {
    override val size = sizeVarLong(value)
    override fun read(stream: AsyncInputStream) {
        value = readVarLong(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readVarLongAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeVarLong(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeVarLongAsync(stream, value)
    }
}

fun Paket.varLong(initial: Long = 0L): Field<Long> = VarLongField(this, initial)

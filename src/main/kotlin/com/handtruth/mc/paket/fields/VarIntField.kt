package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class VarIntField(paket: Paket, initial: Int) : Field<Int>(paket, initial) {
    override val size get() = sizeVarInt(value)
    override fun read(stream: AsyncInputStream) {
        value = readVarInt(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readVarIntAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeVarInt(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeVarIntAsync(stream, value)
    }
}

fun Paket.varInt(initial: Int): Field<Int> = VarIntField(this, initial)

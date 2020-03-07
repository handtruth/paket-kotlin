package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class UInt16Field(paket: Paket, initial: Int): Field<Int>(paket, initial) {
    override val size = sizeShort
    override fun read(stream: AsyncInputStream) {
        value = readShort(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readShortAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeShort(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeShortAsync(stream, value)
    }
}

fun Paket.uint16(initial: Int = 0): Field<Int> = UInt16Field(this, initial)

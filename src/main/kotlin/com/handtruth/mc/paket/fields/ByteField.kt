package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class ByteField(paket: Paket, initial: Byte): Field<Byte>(paket, initial) {
    override val size = sizeByte
    override fun read(stream: AsyncInputStream) {
        value = readByte(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readByteAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeByte(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeByteAsync(stream, value)
    }
}

fun Paket.byte(initial: Byte = 0): Field<Byte> = ByteField(this, initial)

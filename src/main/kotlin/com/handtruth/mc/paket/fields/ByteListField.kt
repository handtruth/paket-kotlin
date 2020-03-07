package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class ByteListField(paket: Paket, initial: MutableList<Byte>) : ListField<Byte>(paket, initial) {
    override fun sizeValue(value: Byte) = sizeByte
    override fun readValue(stream: AsyncInputStream) = readByte(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readByteAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: Byte) = writeByte(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Byte) = writeByteAsync(stream, value)
}

fun Paket.listOfByte(initial: MutableList<Byte> = mutableListOf()): Field<MutableList<Byte>> =
    ByteListField(this, initial)

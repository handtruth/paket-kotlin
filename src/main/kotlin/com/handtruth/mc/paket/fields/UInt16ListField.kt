package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class UInt16ListField(paket: Paket, initial: MutableList<Int>) : ListField<Int>(paket, initial) {
    override fun sizeValue(value: Int) = sizeShort
    override fun readValue(stream: AsyncInputStream) = readShort(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readShortAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: Int) = writeShort(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Int) = writeShortAsync(stream, value)
}

fun Paket.listOfUInt16(initial: MutableList<Int> = mutableListOf()): Field<MutableList<Int>> =
    UInt16ListField(this, initial)

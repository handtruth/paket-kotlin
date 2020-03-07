package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class Int64ListField(paket: Paket, initial: MutableList<Long>) : ListField<Long>(paket, initial) {
    override fun sizeValue(value: Long) = sizeLong
    override fun readValue(stream: AsyncInputStream) = readLong(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readLongAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: Long) = writeLong(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Long) = writeLongAsync(stream, value)
}

fun Paket.listOfInt64(initial: MutableList<Long> = mutableListOf()): Field<MutableList<Long>> =
    Int64ListField(this, initial)

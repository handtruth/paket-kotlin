package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class Int64Field(paket: Paket, initial: Long): Field<Long>(paket, initial) {
    override val size = sizeLong
    override fun read(stream: AsyncInputStream) {
        value = readLong(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readLongAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeLong(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeLongAsync(stream, value)
    }
}

fun Paket.int64(initial: Long): Field<Long> = Int64Field(this, initial)

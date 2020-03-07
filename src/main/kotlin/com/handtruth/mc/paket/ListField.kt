package com.handtruth.mc.paket

open class ListEncoder<T>(val inner: Encoder<T>) : Encoder<MutableList<T>> {
    override fun measure(value: MutableList<T>) = sizeVarInt(value.size) + value.sumBy { inner.measure(it) }
    override fun read(stream: AsyncInputStream, old: MutableList<T>?): MutableList<T> {
        val size = readVarInt(stream)
        val value = old?.apply { clear() } ?: mutableListOf()
        for (i in 1..size)
            value += inner.read(stream, null)
        return value
    }
    override suspend fun readAsync(stream: AsyncInputStream, old: MutableList<T>?): MutableList<T> {
        val size = readVarIntAsync(stream)
        val value = old?.apply { clear() } ?: mutableListOf()
        for (i in 1..size)
            value += inner.readAsync(stream, null)
        return value
    }
    override fun write(stream: AsyncOutputStream, value: MutableList<T>) {
        val size = value.size
        writeVarInt(stream, size)
        value.forEach { inner.write(stream, it) }
    }
    override suspend fun writeAsync(stream: AsyncOutputStream, value: MutableList<T>) {
        val size = value.size
        writeVarIntAsync(stream, size)
        value.forEach { inner.writeAsync(stream, it) }
    }
}

abstract class ListField<T>(encoder: Encoder<MutableList<T>>, initial: MutableList<T>) :
        Field<MutableList<T>>(encoder, initial)

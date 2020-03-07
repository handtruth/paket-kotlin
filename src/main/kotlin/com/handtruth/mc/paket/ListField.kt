package com.handtruth.mc.paket

abstract class ListField<T>(paket: Paket, initial: MutableList<T>) : Field<MutableList<T>>(paket, initial) {
    override val size get() = sizeVarInt(value.size) + value.sumBy { sizeValue(it) }
    override fun read(stream: AsyncInputStream) {
        val size = readVarInt(stream)
        val it = value
        it.clear()
        for (i in 1..size)
            it += readValue(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        val size = readVarIntAsync(stream)
        val it = value
        it.clear()
        for (i in 1..size)
            it += readValueAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        val it = value
        val size = it.size
        writeVarInt(stream, size)
        it.forEach { writeValue(stream, it) }
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        val it = value
        val size = it.size
        writeVarIntAsync(stream, size)
        it.forEach { writeValueAsync(stream, it) }
    }
    protected abstract fun sizeValue(value: T): Int
    protected abstract fun readValue(stream: AsyncInputStream): T
    protected abstract suspend fun readValueAsync(stream: AsyncInputStream): T
    protected abstract fun writeValue(stream: AsyncOutputStream, value: T)
    protected abstract suspend fun writeValueAsync(stream: AsyncOutputStream, value: T)
}

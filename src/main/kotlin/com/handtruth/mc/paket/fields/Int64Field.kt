package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object Int64Encoder : Encoder<Long> {
    override fun measure(value: Long) = sizeLong
    override fun read(stream: AsyncInputStream, old: Long?) = readLong(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: Long?) = readLongAsync(stream)
    override fun write(stream: AsyncOutputStream, value: Long) = writeLong(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: Long) = writeLongAsync(stream, value)
}

object Int64ListEncoder : ListEncoder<Long>(Int64Encoder)

class Int64Field(initial: Long): Field<Long>(Int64Encoder, initial)
class Int64ListField(initial: MutableList<Long>): ListField<Long>(Int64ListEncoder, initial)

fun Paket.int64(initial: Long = 0L) = field(Int64Field(initial))
fun Paket.listOfInt64(initial: MutableList<Long> = mutableListOf()) = field(Int64ListField(initial))
@JvmName("listOfInt64RO")
fun Paket.listOfInt64(initial: List<Long>) = listOfInt64(initial.toMutableList())

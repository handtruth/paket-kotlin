package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object UInt16Encoder : Encoder<Int> {
    override fun measure(value: Int) = sizeShort
    override fun read(stream: AsyncInputStream, old: Int?) = readShort(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: Int?) = readShortAsync(stream)
    override fun write(stream: AsyncOutputStream, value: Int) = writeShort(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: Int) = writeShortAsync(stream, value)
}

object UInt16ListEncoder : ListEncoder<Int>(UInt16Encoder)

class UInt16Field(initial: Int): Field<Int>(UInt16Encoder, initial)
class UInt16ListField(initial: MutableList<Int>): ListField<Int>(UInt16ListEncoder, initial)

fun Paket.uint16(initial: Int = 0) = field(UInt16Field(initial))
fun Paket.listOfUint16(initial: MutableList<Int> = mutableListOf()) = field(UInt16ListField(initial))
@JvmName("listOfUint16RO")
fun Paket.listOfUint16(initial: List<Int>) = listOfUint16(initial.toMutableList())

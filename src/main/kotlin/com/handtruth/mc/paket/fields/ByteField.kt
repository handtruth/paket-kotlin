package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object ByteEncoder : Encoder<Byte> {
    override fun measure(value: Byte) = sizeByte
    override fun read(stream: AsyncInputStream, old: Byte?) = readByte(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: Byte?) = readByteAsync(stream)
    override fun write(stream: AsyncOutputStream, value: Byte) = writeByte(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: Byte) = writeByteAsync(stream, value)
}

object ByteListEncoder : ListEncoder<Byte>(ByteEncoder)

class ByteField(initial: Byte) : Field<Byte>(ByteEncoder, initial)
class ByteListField(initial: MutableList<Byte>) : ListField<Byte>(ByteListEncoder, initial)

fun Paket.byte(initial: Byte = 0) = field(ByteField(initial))
fun Paket.listOfByte(initial: MutableList<Byte>) = field(ByteListField(initial))
@JvmName("listOfByteRO")
fun Paket.listOfByte(initial: List<Byte>) = listOfByte(initial.toMutableList())

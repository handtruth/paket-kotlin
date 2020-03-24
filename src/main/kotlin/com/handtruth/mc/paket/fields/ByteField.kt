package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output

object ByteEncoder : Encoder<Byte> {
    override fun measure(value: Byte) = sizeByte
    override fun read(input: Input, old: Byte?) = readByte(input)
    override fun write(output: Output, value: Byte) = writeByte(output, value)
}

object ByteListEncoder : ListEncoder<Byte>(ByteEncoder)

class ByteField(initial: Byte) : Field<Byte>(ByteEncoder, initial)
class ByteListField(initial: MutableList<Byte>) : ListField<Byte>(ByteListEncoder, initial)

fun Paket.byte(initial: Byte = 0) = field(ByteField(initial))
fun Paket.listOfByte(initial: MutableList<Byte>) = field(ByteListField(initial))
@JvmName("listOfByteRO")
fun Paket.listOfByte(initial: List<Byte>) = listOfByte(initial.toMutableList())

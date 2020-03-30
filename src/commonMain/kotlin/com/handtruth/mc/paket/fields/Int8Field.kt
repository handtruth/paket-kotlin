package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object Int8Codec : Codec<Byte> {
    override fun measure(value: Byte) = sizeByte
    override fun read(input: Input, old: Byte?) = readByte(input)
    override fun write(output: Output, value: Byte) = writeByte(output, value)
}

object Int8ListCodec : ListCodec<Byte>(Int8Codec)

class Int8Field(initial: Byte) : Field<Byte>(Int8Codec, initial)
class Int8ListField(initial: MutableList<Byte>) : ListField<Byte>(Int8ListCodec, initial)

fun Paket.int8(initial: Byte = 0) = field(Int8Field(initial))
fun Paket.listOfInt8(initial: MutableList<Byte>) = field(Int8ListField(initial))
@JvmName("listOfByteRO")
fun Paket.listOfInt8(initial: List<Byte>) = listOfInt8(initial.toMutableList())

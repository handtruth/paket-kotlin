package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object Int16Codec : Codec<Short> {
    override fun measure(value: Short) = sizeShort
    override fun read(input: Input, old: Short?) = readShort(input)
    override fun write(output: Output, value: Short) = writeShort(output, value)
}

object Int16ListCodec : ListCodec<Short>(Int16Codec)

class Int16Field(initial: Short): Field<Short>(Int16Codec, initial)
class Int16ListField(initial: MutableList<Short>): ListField<Short>(Int16ListCodec, initial)

fun Paket.int16(initial: Short = 0) = field(Int16Field(initial))
fun Paket.listOfInt16(initial: MutableList<Short> = mutableListOf()) = field(Int16ListField(initial))
@JvmName("listOfInt16RO")
fun Paket.listOfInt16(initial: List<Short>) = listOfInt16(initial.toMutableList())

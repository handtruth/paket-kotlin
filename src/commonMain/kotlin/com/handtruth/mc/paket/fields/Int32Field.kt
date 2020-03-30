package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object Int32Codec : Codec<Int> {
    override fun measure(value: Int) = sizeInt
    override fun read(input: Input, old: Int?) = readInt(input)
    override fun write(output: Output, value: Int) = writeInt(output, value)
}

object Int32ListCodec : ListCodec<Int>(Int32Codec)

class Int32Field(initial: Int): Field<Int>(Int32Codec, initial)
class Int32ListField(initial: MutableList<Int>): ListField<Int>(Int32ListCodec, initial)

fun Paket.int32(initial: Int = 0) = field(Int32Field(initial))
fun Paket.listOfInt32(initial: MutableList<Int> = mutableListOf()) = field(Int32ListField(initial))
@JvmName("listOfInt32RO")
fun Paket.listOfInt32(initial: List<Int>) = listOfInt32(initial.toMutableList())

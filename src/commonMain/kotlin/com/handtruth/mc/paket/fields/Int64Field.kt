package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object Int64Codec : Codec<Long> {
    override fun measure(value: Long) = sizeLong
    override fun read(input: Input, old: Long?) = readLong(input)
    override fun write(output: Output, value: Long) = writeLong(output, value)
}

object Int64ListCodec : ListCodec<Long>(Int64Codec)

class Int64Field(initial: Long): Field<Long>(Int64Codec, initial)
class Int64ListField(initial: MutableList<Long>): ListField<Long>(Int64ListCodec, initial)

fun Paket.int64(initial: Long = 0L) = field(Int64Field(initial))
fun Paket.listOfInt64(initial: MutableList<Long> = mutableListOf()) = field(Int64ListField(initial))
@JvmName("listOfInt64RO")
fun Paket.listOfInt64(initial: List<Long>) = listOfInt64(initial.toMutableList())

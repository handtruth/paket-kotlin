package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt64Codec : Codec<ULong> {
    override fun measure(value: ULong) = sizeLong
    override fun read(input: Input, old: ULong?) = readLong(input).toULong()
    override fun write(output: Output, value: ULong) = writeLong(output, value.toLong())
}

@ExperimentalPaketApi
object UInt64ListCodec : ListCodec<ULong>(UInt64Codec)

@ExperimentalPaketApi
class UInt64Field(initial: ULong): Field<ULong>(UInt64Codec, initial)
@ExperimentalPaketApi
class UInt64ListField(initial: MutableList<ULong>): ListField<ULong>(UInt64ListCodec, initial)

@ExperimentalPaketApi
fun Paket.uint64(initial: ULong = 0u) = field(UInt64Field(initial))
@ExperimentalPaketApi
fun Paket.listOfUint64(initial: MutableList<ULong> = mutableListOf()) = field(UInt64ListField(initial))
@ExperimentalPaketApi
@JvmName("listOfUint64RO")
fun Paket.listOfUint64(initial: List<ULong>) = listOfUint64(initial.toMutableList())

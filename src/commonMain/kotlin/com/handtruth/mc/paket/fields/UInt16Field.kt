package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt16Codec : Codec<UShort> {
    override fun measure(value: UShort) = sizeShort
    override fun read(input: Input, old: UShort?) = readShort(input).toUShort()
    override fun write(output: Output, value: UShort) = writeShort(output, value.toShort())
}

@ExperimentalPaketApi
object UInt16ListCodec : ListCodec<UShort>(UInt16Codec)

@ExperimentalPaketApi
class UInt16Field(initial: UShort): Field<UShort>(UInt16Codec, initial)
@ExperimentalPaketApi
class UInt16ListField(initial: MutableList<UShort>): ListField<UShort>(UInt16ListCodec, initial)

@ExperimentalPaketApi
fun Paket.uint16(initial: UShort = 0u) = field(UInt16Field(initial))
@ExperimentalPaketApi
fun Paket.listOfUint16(initial: MutableList<UShort> = mutableListOf()) = field(UInt16ListField(initial))
@ExperimentalPaketApi @JvmName("listOfUint16RO")
fun Paket.listOfUint16(initial: List<UShort>) = listOfUint16(initial.toMutableList())

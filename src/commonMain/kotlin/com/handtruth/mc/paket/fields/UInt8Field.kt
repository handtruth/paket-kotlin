package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt8Codec : Codec<UByte> {
    override fun measure(value: UByte) = sizeByte
    override fun read(input: Input, old: UByte?) = readByte(input).toUByte()
    override fun write(output: Output, value: UByte) = writeByte(output, value.toByte())
}

@ExperimentalPaketApi
object UInt8ListCodec : ListCodec<UByte>(UInt8Codec)

@ExperimentalPaketApi
class UInt8Field(initial: UByte): Field<UByte>(UInt8Codec, initial)
@ExperimentalPaketApi
class UInt8ListField(initial: MutableList<UByte>): ListField<UByte>(UInt8ListCodec, initial)

@ExperimentalPaketApi
fun Paket.uint8(initial: UByte = 0u) = field(UInt8Field(initial))
@ExperimentalPaketApi
fun Paket.listOfUint8(initial: MutableList<UByte> = mutableListOf()) = field(UInt8ListField(initial))
@ExperimentalPaketApi
@JvmName("listOfUint8RO")
fun Paket.listOfUint8(initial: List<UByte>) = listOfUint8(initial.toMutableList())

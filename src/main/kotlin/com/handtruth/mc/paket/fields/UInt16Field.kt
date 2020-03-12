package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output

object UInt16Encoder : Encoder<UShort> {
    override fun measure(value: UShort) = sizeShort
    override fun read(input: Input, old: UShort?) = readShort(input).toUShort()
    override fun write(output: Output, value: UShort) = writeShort(output, value.toShort())
}

object UInt16ListEncoder : ListEncoder<UShort>(UInt16Encoder)

class UInt16Field(initial: UShort): Field<UShort>(UInt16Encoder, initial)
class UInt16ListField(initial: MutableList<UShort>): ListField<UShort>(UInt16ListEncoder, initial)

fun Paket.uint16(initial: UShort = 0u) = field(UInt16Field(initial))
fun Paket.listOfUint16(initial: MutableList<UShort> = mutableListOf()) = field(UInt16ListField(initial))
@JvmName("listOfUint16RO")
fun Paket.listOfUint16(initial: List<UShort>) = listOfUint16(initial.toMutableList())

package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object UInt32Codec : Codec<UInt> {
    override fun measure(value: UInt) = sizeInt
    override fun read(input: Input, old: UInt?) = readInt(input).toUInt()
    override fun write(output: Output, value: UInt) = writeInt(output, value.toInt())
}

@ExperimentalPaketApi
object UInt32ListCodec : ListCodec<UInt>(UInt32Codec)

@ExperimentalPaketApi
class UInt32Field(initial: UInt): Field<UInt>(UInt32Codec, initial)
@ExperimentalPaketApi
class UInt32ListField(initial: MutableList<UInt>): ListField<UInt>(UInt32ListCodec, initial)

@ExperimentalPaketApi
fun Paket.uint32(initial: UInt = 0u) = field(UInt32Field(initial))
@ExperimentalPaketApi
fun Paket.listOfUint32(initial: MutableList<UInt> = mutableListOf()) = field(UInt32ListField(initial))
@ExperimentalPaketApi
@JvmName("listOfUint32RO")
fun Paket.listOfUint32(initial: List<UInt>) = listOfUint32(initial.toMutableList())

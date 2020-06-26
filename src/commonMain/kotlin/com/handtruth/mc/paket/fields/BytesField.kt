package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Bytes
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.io.buildBytes
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object BytesCodec : Codec<Bytes> {
    override fun measure(value: Bytes) = sizeBytes(value)
    override fun read(input: Input, old: Bytes?) = readBytes(input)
    override fun write(output: Output, value: Bytes) = writeBytes(output, value)
}

@ExperimentalPaketApi
object BytesListCodec : ListCodec<Bytes>(BytesCodec)

@ExperimentalPaketApi
class BytesField(initial: Bytes) : Field<Bytes>(BytesCodec, initial)

@ExperimentalPaketApi
class BytesListField(initial: MutableList<Bytes>) : ListField<Bytes>(BytesListCodec, initial)

@ExperimentalPaketApi
fun Paket.bytes(initial: Bytes = buildBytes { }) = field(BytesField(initial))

@ExperimentalPaketApi
fun Paket.listOfBytes(initial: MutableList<Bytes>) = field(BytesListField(initial))

@ExperimentalPaketApi
@JvmName("listOfBytesRO")
fun Paket.listOfBytes(initial: List<Bytes>) = listOfBytes(initial.toMutableList())

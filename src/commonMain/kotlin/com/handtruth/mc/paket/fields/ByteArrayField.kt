package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object ByteArrayCodec : Codec<ByteArray> {
    override fun measure(value: ByteArray) = sizeByteArray(value)
    override fun read(input: Input, old: ByteArray?) = readByteArray(input)
    override fun write(output: Output, value: ByteArray) = writeByteArray(output, value)
}

object ByteArrayListCodec : ListCodec<ByteArray>(ByteArrayCodec)

class ByteArrayField(initial: ByteArray) : Field<ByteArray>(ByteArrayCodec, initial)
class ByteArrayListField(initial: MutableList<ByteArray>) : ListField<ByteArray>(ByteArrayListCodec, initial)

fun Paket.byteArray(initial: ByteArray = ByteArray(0)) = field(ByteArrayField(initial))
fun Paket.listOfByteArray(initial: MutableList<ByteArray>) = field(ByteArrayListField(initial))
@JvmName("listOfByteArrayRO")
fun Paket.listOfByteArray(initial: List<ByteArray>) = listOfByteArray(initial.toMutableList())

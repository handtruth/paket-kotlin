package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.readBoolean
import com.handtruth.mc.paket.sizeBoolean
import com.handtruth.mc.paket.writeBoolean
import kotlinx.io.Input
import kotlinx.io.Output

object ByteArrayEncoder : Encoder<ByteArray> {
    override fun measure(value: ByteArray) = sizeByteArray(value)
    override fun read(input: Input, old: ByteArray?) = readByteArray(input)
    override fun write(output: Output, value: ByteArray) = writeByteArray(output, value)
}

object ByteArrayListEncoder : ListEncoder<ByteArray>(ByteArrayEncoder)

class ByteArrayField(initial: ByteArray) : Field<ByteArray>(ByteArrayEncoder, initial)
class ByteArrayListField(initial: MutableList<ByteArray>) : ListField<ByteArray>(ByteArrayListEncoder, initial)

fun Paket.byteArray(initial: ByteArray = ByteArray(0)) = field(ByteArrayField(initial))
fun Paket.listOfByteArray(initial: MutableList<ByteArray>) = field(ByteArrayListField(initial))
@JvmName("listOfByteArrayRO")
fun Paket.listOfByteArray(initial: List<ByteArray>) = listOfByteArray(initial.toMutableList())

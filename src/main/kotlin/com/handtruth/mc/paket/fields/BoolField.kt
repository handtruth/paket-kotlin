package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object BoolEncoder : Encoder<Boolean> {
    override fun measure(value: Boolean) = sizeBoolean
    override fun read(stream: AsyncInputStream, old: Boolean?) = readBoolean(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: Boolean?) = readBooleanAsync(stream)
    override fun write(stream: AsyncOutputStream, value: Boolean) = writeBoolean(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: Boolean) = writeBooleanAsync(stream, value)
}

object BoolListEncoder : ListEncoder<Boolean>(BoolEncoder)

class BoolField(initial: Boolean) : Field<Boolean>(BoolEncoder, initial)
class BoolListField(initial: MutableList<Boolean>) : ListField<Boolean>(BoolListEncoder, initial)

fun Paket.bool(initial: Boolean = false) = field(BoolField(initial))
fun Paket.listOfBool(initial: MutableList<Boolean>) = field(BoolListField(initial))
@JvmName("listOfBoolRO")
fun Paket.listOfBool(initial: List<Boolean>) = listOfBool(initial.toMutableList())

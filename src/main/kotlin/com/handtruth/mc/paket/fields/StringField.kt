package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object StringEncoder : Encoder<String> {
    override fun measure(value: String) = sizeString(value)
    override fun read(stream: AsyncInputStream, old: String?) = readString(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: String?) = readStringAsync(stream)
    override fun write(stream: AsyncOutputStream, value: String) = writeString(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: String) = writeStringAsync(stream, value)
}

object StringListEncoder : ListEncoder<String>(StringEncoder)

class StringField(initial: String) : Field<String>(StringEncoder, initial)
class StringListField(initial: MutableList<String>) : ListField<String>(StringListEncoder, initial)

fun Paket.string(initial: String = "") = field(StringField(initial))
fun Paket.listOfString(initial: MutableList<String> = mutableListOf()) = field(StringListField(initial))
@JvmName("listOfStringRO")
fun Paket.listOfString(initial: List<String>) = listOfString(initial.toMutableList())

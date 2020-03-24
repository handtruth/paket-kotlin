package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output

object StringEncoder : Encoder<String> {
    override fun measure(value: String) = sizeString(value)
    override fun read(input: Input, old: String?) = readString(input)
    override fun write(output: Output, value: String) = writeString(output, value)
}

object StringListEncoder : ListEncoder<String>(StringEncoder)

class StringField(initial: String) : Field<String>(StringEncoder, initial)
class StringListField(initial: MutableList<String>) : ListField<String>(StringListEncoder, initial)

fun Paket.string(initial: String = "") = field(StringField(initial))
fun Paket.listOfString(initial: MutableList<String> = mutableListOf()) = field(StringListField(initial))
@JvmName("listOfStringRO")
fun Paket.listOfString(initial: List<String>) = listOfString(initial.toMutableList())

package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output

object BoolEncoder : Encoder<Boolean> {
    override fun measure(value: Boolean) = sizeBoolean
    override fun read(input: Input, old: Boolean?) = readBoolean(input)
    override fun write(output: Output, value: Boolean) = writeBoolean(output, value)
}

object BoolListEncoder : ListEncoder<Boolean>(BoolEncoder)

class BoolField(initial: Boolean) : Field<Boolean>(BoolEncoder, initial)
class BoolListField(initial: MutableList<Boolean>) : ListField<Boolean>(BoolListEncoder, initial)

fun Paket.bool(initial: Boolean = false) = field(BoolField(initial))
fun Paket.listOfBool(initial: MutableList<Boolean>) = field(BoolListField(initial))
@JvmName("listOfBoolRO")
fun Paket.listOfBool(initial: List<Boolean>) = listOfBool(initial.toMutableList())

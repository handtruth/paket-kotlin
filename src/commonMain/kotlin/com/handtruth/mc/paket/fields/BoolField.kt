package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object BoolCodec : Codec<Boolean> {
    override fun measure(value: Boolean) = sizeBoolean
    override fun read(input: Input, old: Boolean?) = readBoolean(input)
    override fun write(output: Output, value: Boolean) = writeBoolean(output, value)
}

object BoolListCodec : ListCodec<Boolean>(BoolCodec)

class BoolField(initial: Boolean) : Field<Boolean>(BoolCodec, initial)
class BoolListField(initial: MutableList<Boolean>) : ListField<Boolean>(BoolListCodec, initial)

fun Paket.bool(initial: Boolean = false) = field(BoolField(initial))
fun Paket.listOfBool(initial: MutableList<Boolean>) = field(BoolListField(initial))
@JvmName("listOfBoolRO")
fun Paket.listOfBool(initial: List<Boolean>) = listOfBool(initial.toMutableList())

package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object FloatCodec : Codec<Float> {
    override fun measure(value: Float) = sizeFloat
    override fun read(input: Input, old: Float?) = readFloat(input)
    override fun write(output: Output, value: Float) = writeFloat(output, value)
}

object FloatListCodec : ListCodec<Float>(FloatCodec)

class FloatField(initial: Float): Field<Float>(FloatCodec, initial)
class FloatListField(initial: MutableList<Float>): ListField<Float>(FloatListCodec, initial)

fun Paket.float(initial: Float = 0f) = field(FloatField(initial))
fun Paket.listOfFloat(initial: MutableList<Float> = mutableListOf()) = field(FloatListField(initial))
@JvmName("listOfFloatRO")
fun Paket.listOfFloat(initial: List<Float>) = listOfFloat(initial.toMutableList())

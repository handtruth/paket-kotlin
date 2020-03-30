package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object DoubleCodec : Codec<Double> {
    override fun measure(value: Double) = sizeDouble
    override fun read(input: Input, old: Double?) = readDouble(input)
    override fun write(output: Output, value: Double) = writeDouble(output, value)
}

object DoubleListCodec : ListCodec<Double>(DoubleCodec)

class DoubleField(initial: Double): Field<Double>(DoubleCodec, initial)
class DoubleListField(initial: MutableList<Double>): ListField<Double>(DoubleListCodec, initial)

fun Paket.double(initial: Double = .0) = field(DoubleField(initial))
fun Paket.listOfDouble(initial: MutableList<Double> = mutableListOf()) = field(DoubleListField(initial))
@JvmName("listOfDoubleRO")
fun Paket.listOfDouble(initial: List<Double>) = listOfDouble(initial.toMutableList())

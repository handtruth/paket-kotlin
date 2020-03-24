package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output

object VarIntEncoder : Encoder<Int> {
    override fun measure(value: Int) = sizeVarInt(value)
    override fun read(input: Input, old: Int?) = readVarInt(input)
    override fun write(output: Output, value: Int) = writeVarInt(output, value)
}

object VarIntListEncoder : ListEncoder<Int>(VarIntEncoder)

class VarIntField(initial: Int) : Field<Int>(VarIntEncoder, initial)
class VarIntListField(initial: MutableList<Int>) : ListField<Int>(VarIntListEncoder, initial)

fun Paket.varInt(initial: Int = 0) = field(VarIntField(initial))
fun Paket.listOfVarInt(initial: MutableList<Int> = mutableListOf()) = field(VarIntListField(initial))
@JvmName("listOfVarIntRO")
fun Paket.listOfVarInt(initial: List<Int>) = listOfVarInt(initial.toMutableList())

package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

object VarLongCodec : Codec<Long> {
    override fun measure(value: Long) = sizeVarLong(value)
    override fun read(input: Input, old: Long?) = readVarLong(input)
    override fun write(output: Output, value: Long) = writeVarLong(output, value)
}

object VarLongListCodec : ListCodec<Long>(VarLongCodec)

class VarLongField(initial: Long) : Field<Long>(VarLongCodec, initial)
class VarLongListField(initial: MutableList<Long>) : ListField<Long>(VarLongListCodec, initial)

fun Paket.varLong(initial: Long = 0L) = field(VarLongField(initial))
fun Paket.listOfVarLong(initial: MutableList<Long> = mutableListOf()) = field(VarLongListField(initial))
@JvmName("listOfVarLongRO")
fun Paket.listOfVarLong(initial: List<Long>) = listOfVarLong(initial.toMutableList())

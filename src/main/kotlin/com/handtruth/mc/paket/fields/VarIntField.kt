package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object VarIntEncoder : Encoder<Int> {
    override fun measure(value: Int) = sizeVarInt(value)
    override fun read(stream: AsyncInputStream, old: Int?) = readVarInt(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: Int?) = readVarIntAsync(stream)
    override fun write(stream: AsyncOutputStream, value: Int) = writeVarInt(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: Int) = writeVarIntAsync(stream, value)
}

object VarIntListEncoder : ListEncoder<Int>(VarIntEncoder)

class VarIntField(initial: Int) : Field<Int>(VarIntEncoder, initial)
class VarIntListField(initial: MutableList<Int>) : ListField<Int>(VarIntListEncoder, initial)

fun Paket.varInt(initial: Int = 0) = field(VarIntField(initial))
fun Paket.listOfVarInt(initial: MutableList<Int> = mutableListOf()) = field(VarIntListField(initial))
@JvmName("listOfVarIntRO")
fun Paket.listOfVarInt(initial: List<Int>) = listOfVarInt(initial.toMutableList())

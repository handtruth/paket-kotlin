package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object VarLongEncoder : Encoder<Long> {
    override fun measure(value: Long) = sizeVarLong(value)
    override fun read(stream: AsyncInputStream, old: Long?) = readVarLong(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: Long?) = readVarLongAsync(stream)
    override fun write(stream: AsyncOutputStream, value: Long) = writeVarLong(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: Long) = writeVarLongAsync(stream, value)
}

object VarLongListEncoder : ListEncoder<Long>(VarLongEncoder)

class VarLongField(initial: Long) : Field<Long>(VarLongEncoder, initial)
class VarLongListField(initial: MutableList<Long>) : ListField<Long>(VarLongListEncoder, initial)

fun Paket.varLong(initial: Long = 0L) = field(VarLongField(initial))
fun Paket.listOfVarLong(initial: MutableList<Long> = mutableListOf()) = field(VarLongListField(initial))
@JvmName("listOfVarLongRO")
fun Paket.listOfVarLong(initial: List<Long>) = listOfVarLong(initial.toMutableList())

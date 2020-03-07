package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.sizeVarLong

private class VarLongListField(paket: Paket, initial: MutableList<Long>) : ListField<Long>(paket, initial) {
    override fun sizeValue(value: Long) = sizeVarLong(value)
    override fun readValue(stream: AsyncInputStream) = readVarLong(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readVarLongAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: Long) = writeVarLong(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Long) = writeVarLongAsync(stream, value)
}

fun Paket.listOfVarLong(initial: MutableList<Long> = mutableListOf()): Field<MutableList<Long>> =
    VarLongListField(this, initial)

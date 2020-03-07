package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class VarIntList(paket: Paket, initial: MutableList<Int>) : ListField<Int>(paket, initial) {
    override fun sizeValue(value: Int) = sizeVarInt(value)
    override fun readValue(stream: AsyncInputStream) = readVarInt(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readVarIntAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: Int) = writeVarInt(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Int) = writeVarIntAsync(stream, value)
}

fun Paket.listOfVarInt(initial: MutableList<Int> = mutableListOf()): Field<MutableList<Int>> =
    VarIntList(this, initial)

package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class BoolListField(paket: Paket, initial: MutableList<Boolean>) : ListField<Boolean>(paket, initial) {
    override fun sizeValue(value: Boolean) = sizeBoolean
    override fun readValue(stream: AsyncInputStream) = readBoolean(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readBooleanAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: Boolean) = writeBoolean(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Boolean) = writeBooleanAsync(stream, value)
}

fun Paket.listOfBoolean(initial: MutableList<Boolean> = mutableListOf()): Field<MutableList<Boolean>> =
    BoolListField(this, initial)

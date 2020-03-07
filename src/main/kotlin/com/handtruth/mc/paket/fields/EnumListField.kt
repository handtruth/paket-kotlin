package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class EnumListField<E: Enum<E>>(paket: Paket, initial: MutableList<E>,
                                        val values: Array<E>) : ListField<E>(paket, initial) {
    override fun sizeValue(value: E) = sizeVarInt(value.ordinal)
    override fun readValue(stream: AsyncInputStream) = values[readVarInt(stream)]
    override suspend fun readValueAsync(stream: AsyncInputStream) = values[readVarIntAsync(stream)]
    override fun writeValue(stream: AsyncOutputStream, value: E) = writeVarInt(stream, value.ordinal)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: E) = writeVarIntAsync(stream, value.ordinal)
}

fun <E: Enum<E>> Paket.listOfEnum(values: Array<E>, initial: MutableList<E> = mutableListOf()): Field<MutableList<E>> =
    EnumListField(this, initial, values)

inline fun <reified E: Enum<E>> Paket.listOfEnum(initial: MutableList<E> = mutableListOf()): Field<MutableList<E>> =
    listOfEnum(enumValues(), initial)

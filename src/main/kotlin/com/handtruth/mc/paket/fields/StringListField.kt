package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class StringListField(paket: Paket, initial: MutableList<String>) : ListField<String>(paket, initial) {
    override fun sizeValue(value: String) = sizeString(value)
    override fun readValue(stream: AsyncInputStream) = readString(stream)
    override suspend fun readValueAsync(stream: AsyncInputStream) = readStringAsync(stream)
    override fun writeValue(stream: AsyncOutputStream, value: String) = writeString(stream, value)
    override suspend fun writeValueAsync(stream: AsyncOutputStream, value: String) = writeStringAsync(stream, value)
}

fun Paket.listOfString(initial: MutableList<String> = mutableListOf()): Field<MutableList<String>> =
    StringListField(this, initial)

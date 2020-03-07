package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

class StringField(paket: Paket, initial: String) : Field<String>(paket, initial) {
    override val size get() = sizeString(value)
    override fun read(stream: AsyncInputStream) {
        value = readString(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readStringAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeString(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeStringAsync(stream, value)
    }
    override fun toString() = "\"$value\""
}

fun Paket.string(initial: String): Field<String> = StringField(this, initial)

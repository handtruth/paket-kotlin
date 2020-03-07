package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

private class BoolField(paket: Paket, initial: Boolean) : Field<Boolean>(paket, initial) {
    override val size = sizeBoolean
    override fun read(stream: AsyncInputStream) {
        value = readBoolean(stream)
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = readBooleanAsync(stream)
    }
    override fun write(stream: AsyncOutputStream) {
        writeBoolean(stream, value)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeBooleanAsync(stream, value)
    }
}

fun Paket.bool(initial: Boolean = false): Field<Boolean> = BoolField(this, initial)

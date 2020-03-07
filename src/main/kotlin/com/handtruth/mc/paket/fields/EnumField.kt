package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlin.reflect.full.staticFunctions

private class EnumField<E: Enum<E>>(paket: Paket, val values: Array<E>, initial: E) : Field<E>(paket, initial) {
    override val size get() = sizeVarInt(value.ordinal)
    override fun read(stream: AsyncInputStream) {
        value = values[readVarInt(stream)]
    }
    override suspend fun readAsync(stream: AsyncInputStream) {
        value = values[readVarIntAsync(stream)]
    }
    override fun write(stream: AsyncOutputStream) {
        writeVarInt(stream, value.ordinal)
    }
    override suspend fun writeAsync(stream: AsyncOutputStream) {
        writeVarIntAsync(stream, value.ordinal)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified E: Enum<E>> enumValues() =
    E::class.staticFunctions.find { it.name == "values" }!!.call() as Array<E>

fun <E: Enum<E>> Paket.enumField(values: Array<E>, initial: E = values.first()): Field<E> =
    EnumField(this, values, initial)
inline fun <reified E: Enum<E>> Paket.enumField(): Field<E> = enumField(enumValues())
inline fun <reified E: Enum<E>> Paket.enumField(initial: E): Field<E> = enumField(enumValues(), initial)

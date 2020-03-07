package com.handtruth.mc.paket

import kotlin.reflect.KProperty

open class Field<T>(private val encoder: Encoder<T>, initial: T) {
    val size: Int get() = encoder.measure(value)
    fun read(stream: AsyncInputStream) {
        value = encoder.read(stream, value)
    }
    suspend fun readAsync(stream: AsyncInputStream) {
        value = encoder.readAsync(stream, value)
    }
    fun write(stream: AsyncOutputStream) {
        encoder.write(stream, value)
    }
    suspend fun writeAsync(stream: AsyncOutputStream) {
        encoder.writeAsync(stream, value)
    }

    var value: T = initial
    operator fun getValue(me: Paket, property: KProperty<*>): T {
        return value
    }
    operator fun setValue(me: Paket, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun equals(other: Any?) = other is Field<*> && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = value.toString()
}

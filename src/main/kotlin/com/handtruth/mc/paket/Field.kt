package com.handtruth.mc.paket

import kotlin.reflect.KProperty

abstract class Field<T>(paket: Paket, initial: T) {
    init {
        @Suppress("LeakingThis")
        paket.fields += this
    }

    abstract val size: Int
    abstract fun read(stream: AsyncInputStream)
    abstract suspend fun readAsync(stream: AsyncInputStream)
    abstract fun write(stream: AsyncOutputStream)
    abstract suspend fun writeAsync(stream: AsyncOutputStream)

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

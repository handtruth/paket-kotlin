package com.handtruth.mc.paket

import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

open class Field<T>(private val codec: Codec<T>, initial: T) {
    val size: Int get() = codec.measure(value)
    fun read(input: Input) {
        value = codec.read(input, value)
    }
    fun write(output: Output) {
        codec.write(output, value)
    }

    var value: T = initial
    operator fun getValue(me: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(me: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    override fun equals(other: Any?) = other is Field<*> && value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = value.toString()
}

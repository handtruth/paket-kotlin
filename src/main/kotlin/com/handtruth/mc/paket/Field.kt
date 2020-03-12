package com.handtruth.mc.paket

import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

open class Field<T>(private val encoder: Encoder<T>, initial: T) {
    val size: Int get() = encoder.measure(value)
    fun read(input: Input) {
        value = encoder.read(input, value)
    }
    fun write(output: Output) {
        encoder.write(output, value)
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

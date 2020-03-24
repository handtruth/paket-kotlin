package com.handtruth.mc.paket

import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KClass

open class ListEncoder<T>(val inner: Encoder<T>) : Encoder<MutableList<T>> {
    override fun measure(value: MutableList<T>) = sizeVarInt(value.size) + value.sumBy { inner.measure(it) }
    override fun read(input: Input, old: MutableList<T>?): MutableList<T> {
        val size = readVarInt(input)
        val value = old?.apply { clear() } ?: mutableListOf()
        for (i in 1..size)
            value += inner.read(input, null)
        return value
    }
    override fun write(output: Output, value: MutableList<T>) {
        val size = value.size
        writeVarInt(output, size)
        value.forEach { inner.write(output, it) }
    }
}

abstract class ListField<T>(encoder: Encoder<MutableList<T>>, initial: MutableList<T>) :
        Field<MutableList<T>>(encoder, initial)

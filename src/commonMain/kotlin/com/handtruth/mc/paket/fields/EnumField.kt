package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

class EnumCodec<T: Enum<T>>(private val all: Array<T>) : Codec<T> {
    override fun measure(value: T) = sizeVarInt(value.ordinal)
    override fun read(input: Input, old: T?) = all[readVarInt(input)]
    override fun write(output: Output, value: T) = writeVarInt(output, value.ordinal)

    val values get() = all.asSequence()

    companion object {
        inline operator fun <reified E: Enum<E>> invoke() = EnumCodec(enumValues<E>())
    }
}

class EnumListCodec<T: Enum<T>>(values: Array<T>) : ListCodec<T>(EnumCodec(values)) {
    val values get() = (inner as EnumCodec<T>).values

    companion object {
        inline operator fun <reified E: Enum<E>> invoke() = EnumListCodec(enumValues<E>())
    }
}

class EnumField<E: Enum<E>>(initial: E, values: Array<E>) : Field<E>(EnumCodec(values), initial) {
    companion object {
        inline operator fun <reified E: Enum<E>> invoke(initial: E) = EnumField(initial, enumValues())
    }
}
class EnumListField<E: Enum<E>>(initial: MutableList<E>, values: Array<E>) :
    ListField<E>(EnumListCodec(values), initial) {

    companion object {
        inline operator fun <reified E: Enum<E>> invoke(initial: MutableList<E>) = EnumListField(initial, enumValues())
    }
}

inline fun <reified E: Enum<E>> Paket.enum(initial: E = enumValues<E>()[0]) = field(EnumField(initial))
inline fun <reified E: Enum<E>> Paket.listOfEnum(initial: MutableList<E> = mutableListOf()) =
    field(EnumListField(initial, enumValues()))
@JvmName("listOfEnumRO")
inline fun <reified E: Enum<E>> Paket.listOfEnum(initial: List<E>) = listOfEnum(initial.toMutableList())

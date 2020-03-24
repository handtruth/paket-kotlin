package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

class EnumEncoder<T: Enum<T>> private constructor(private val all: Array<T>) : Encoder<T> {
    override fun measure(value: T) = sizeVarInt(value.ordinal)
    override fun read(input: Input, old: T?) = all[readVarInt(input)]
    override fun write(output: Output, value: T) = writeVarInt(output, value.ordinal)

    val values get() = all.asSequence()

    companion object {
        private val enumEncoders: MutableMap<KClass<out Enum<*>>, EnumEncoder<*>> = ConcurrentHashMap()
        operator fun <E: Enum<E>> invoke(type: KClass<out Enum<E>>): EnumEncoder<E> {
            val result = enumEncoders[type]
            @Suppress("UNCHECKED_CAST")
            return if (result == null) {
                val values = type.staticFunctions.find { it.name == "values" }!!.call() as Array<E>
                val encoder = EnumEncoder(values)
                enumEncoders[type] = encoder
                encoder
            } else {
                result as EnumEncoder<E>
            }
        }
        inline operator fun <reified E: Enum<E>> invoke() = invoke(E::class)
    }
}

class EnumListEncoder<T: Enum<T>> private constructor(encoder: EnumEncoder<T>) : ListEncoder<T>(encoder) {

    val values get() = (inner as EnumEncoder<T>).values

    companion object {
        private val encoders: MutableMap<KClass<out Enum<*>>, EnumListEncoder<*>> = ConcurrentHashMap()
        operator fun <E: Enum<E>> invoke(type: KClass<out Enum<E>>): EnumListEncoder<E> {
            val result = encoders[type]
            @Suppress("UNCHECKED_CAST")
            return if (result == null) {
                val encoder = EnumListEncoder(EnumEncoder(type))
                encoders[type] = encoder
                encoder
            } else {
                result as EnumListEncoder<E>
            }
        }
        inline operator fun <reified E: Enum<E>> invoke() = EnumEncoder.invoke(E::class)
    }
}

class EnumField<E: Enum<E>> private constructor(encoder: Encoder<E>, initial: E) : Field<E>(encoder, initial) {
    companion object {
        operator fun <E: Enum<E>> invoke(type: KClass<out E>): EnumField<E> {
            val encoder = EnumEncoder(type)
            return EnumField(encoder, encoder.values.first())
        }
        operator fun <E: Enum<E>> invoke(type: KClass<out E>, initial: E): EnumField<E> {
            return EnumField(EnumEncoder(type), initial)
        }
        inline operator fun <reified E: Enum<E>> invoke(initial: E) = EnumField(E::class, initial)
        inline operator fun <reified E: Enum<E>> invoke() = EnumField(E::class)
    }
}
class EnumListField<E: Enum<E>>(type: KClass<E>, initial: MutableList<E>) : ListField<E>(EnumListEncoder(type), initial)

inline fun <reified E: Enum<E>> Paket.enum(initial: E) = field(EnumField(initial))
inline fun <reified E: Enum<E>> Paket.enum() = field(EnumField<E>())
inline fun <reified E: Enum<E>> Paket.listOfEnum(initial: MutableList<E> = mutableListOf()) =
    field(EnumListField(E::class, initial))
@JvmName("listOfEnumRO")
inline fun <reified E: Enum<E>> Paket.listOfEnum(initial: List<E>) = listOfEnum(initial.toMutableList())

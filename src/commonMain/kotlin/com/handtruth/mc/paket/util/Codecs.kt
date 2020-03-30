package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.fields.*
import kotlin.reflect.KClass

object Codecs {
    private val allCodecs: MutableMap<KClass<out Codec<*>>, Codec<*>> = hashMapOf<KClass<out Codec<*>>, Codec<*>>()
        .also { result ->
            arrayOf(
                BoolCodec, Int8Codec, UInt8Codec, Int16Codec, UInt16Codec, Int32Codec, UInt32Codec, Int64Codec, UInt64Codec,
                FloatCodec, DoubleCodec, VarIntCodec, VarLongCodec, StringCodec
            ).associateByTo(result) { it::class }
        }

    fun <T> register(codec: Codec<T>) {
        allCodecs[codec::class] = codec
    }

    private fun genError(`class`: KClass<out Codec<*>>): Nothing = error("Codec $`class` not registered")

    fun <T> getInstance(`class`: KClass<out Codec<T>>): Codec<T> {
        @Suppress("UNCHECKED_CAST")
        return allCodecs[`class`] as? Codec<T> ?: genError(`class`)
    }
}

package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.Field
import com.handtruth.mc.paket.Paket
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer

class StringFormatCodec<T>(val serializer: KSerializer<T>, val format: StringFormat) : Codec<T> {
    override fun measure(value: T) = StringCodec.measure(format.stringify(serializer, value))
    override fun read(input: Input, old: T?) = format.parse(serializer, StringCodec.read(input, null))
    override fun write(output: Output, value: T) = StringCodec.write(output, format.stringify(serializer, value))
}

class StringFormatField<T>(initial: T, serializer: KSerializer<T>, format: StringFormat) :
    Field<T>(StringFormatCodec(serializer, format), initial)

fun <T> Paket.string(format: StringFormat, initial: T, serializer: KSerializer<T>) =
    field(StringFormatField(initial, serializer, format))
inline fun <reified T: Any> Paket.string(format: StringFormat, initial: T) =
    string(format, initial, T::class.serializer())

fun <T> Paket.json(initial: T, serializer: KSerializer<T>) =
    string(Json(JsonConfiguration.Stable), initial, serializer)

inline fun <reified T: Any> Paket.json(initial: T) = json(initial, T::class.serializer())

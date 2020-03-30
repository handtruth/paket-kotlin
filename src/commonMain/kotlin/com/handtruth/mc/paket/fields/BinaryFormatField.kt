package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.Codec
import com.handtruth.mc.paket.Field
import com.handtruth.mc.paket.Paket
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class BinaryFormatCodec<T>(val serializer: KSerializer<T>, val format: BinaryFormat) : Codec<T> {
    override fun measure(value: T) = ByteArrayCodec.measure(format.dump(serializer, value))
    override fun read(input: Input, old: T?) = format.load(serializer, ByteArrayCodec.read(input, null))
    override fun write(output: Output, value: T) = ByteArrayCodec.write(output, format.dump(serializer, value))
}

class BinaryFormatField<T>(initial: T, serializer: KSerializer<T>, format: BinaryFormat) :
    Field<T>(BinaryFormatCodec(serializer, format), initial)

fun <T> Paket.binary(format: BinaryFormat, initial: T, serializer: KSerializer<T>) =
    field(BinaryFormatField(initial, serializer, format))
inline fun <reified T: Any> Paket.binary(format: BinaryFormat, initial: T) = binary(format, initial, T::class.serializer())

fun <T> Paket.serial(initial: T, serializer: KSerializer<T>) = binary(Paket, initial, serializer)
inline fun <reified T: Any> Paket.serial(initial: T) = serial(initial, T::class.serializer())

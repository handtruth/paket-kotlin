package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.*
import kotlinx.io.Output
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

class PaketEncoder(
    private val output: Output,
    override val context: SerialModule = EmptyModule
) : Encoder, CompositeEncoder {
    init {
        //println("CREATED ENCODER")
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
        vararg typeSerializers: KSerializer<*>
    ): CompositeEncoder {
        writeVarInt(output, collectionSize)
        return super.beginCollection(descriptor, collectionSize, *typeSerializers)
    }

    private inline fun <T> encode(descriptor: SerialDescriptor, index: Int, value: T, fallback: () -> Unit) {
        if (isMapOrList(descriptor))
            return fallback()
        //println("encode: d=${descriptor}, e=${descriptor.getElementDescriptor(index)}")
        val codec = codecOf<T>(descriptor, index) ?: return fallback()
        codec.write(output, value)
    }

    override fun beginStructure(
        descriptor: SerialDescriptor,
        vararg typeSerializers: KSerializer<*>
    ): CompositeEncoder {
        return this
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        if (value == null)
            forbidNulls()
        encodeSerializableElement(descriptor, index, serializer, value)
    }

    override fun encodeNotNullMark() {}

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        //println("encodeSerializableElement: d=${descriptor}")
        when (value) {
            is Boolean -> encodeBooleanElement(descriptor, index, value)
            is Byte -> encodeByteElement(descriptor, index, value)
            is Short -> encodeShortElement(descriptor, index, value)
            is Int -> encodeIntElement(descriptor, index, value)
            is Long -> encodeLongElement(descriptor, index, value)
            is Float -> encodeFloatElement(descriptor, index, value)
            is Double -> encodeDoubleElement(descriptor, index, value)
            is Char -> encodeCharElement(descriptor, index, value)
            is String -> encodeStringElement(descriptor, index, value)
            is Unit -> {}
            else -> serializer.serialize(this, value)
        }
    }

    override fun encodeBoolean(value: Boolean) = writeBoolean(output, value)
    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
        encode(descriptor, index, value) { writeBoolean(output, value) }
    override fun encodeByte(value: Byte) = writeByte(output, value)
    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
        encode(descriptor, index, value) { writeByte(output, value) }
    override fun encodeChar(value: Char) = writeShort(output, value.toShort())
    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
        encode(descriptor, index, value) { writeShort(output, value.toShort()) }
    override fun encodeDouble(value: Double) = writeDouble(output, value)
    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
        encode(descriptor, index, value) { writeDouble(output, value) }
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = writeVarInt(output, index)
    override fun encodeFloat(value: Float) = writeFloat(output, value)
    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
        encode(descriptor, index, value) { writeFloat(output, value) }
    override fun encodeInt(value: Int) = writeVarInt(output, value)
    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
        encode(descriptor, index, value) { writeVarInt(output, value) }
    override fun encodeLong(value: Long) = writeVarLong(output, value)
    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
        encode(descriptor, index, value) { writeVarLong(output, value) }
    override fun encodeNull() = forbidNulls()
    override fun encodeShort(value: Short) = writeShort(output, value)
    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
        encode(descriptor, index, value) { writeShort(output, value) }
    override fun encodeString(value: String) = writeString(output, value)
    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
        encode(descriptor, index, value) { writeString(output, value) }
    override fun encodeUnit() {}
    override fun encodeUnitElement(descriptor: SerialDescriptor, index: Int) {}

    override fun endStructure(descriptor: SerialDescriptor) {}
}

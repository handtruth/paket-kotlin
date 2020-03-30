package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptyModule
import kotlinx.serialization.modules.SerialModule

class PaketDecoder(
    private val input: Input,
    override val context: SerialModule = EmptyModule,
    override val updateMode: UpdateMode = UpdateMode.BANNED
) : Decoder, CompositeDecoder {
    init {
        //println("CREATED DECODER")
    }

    private inline fun <reified T> decode(descriptor: SerialDescriptor, index: Int, fallback: () -> T): T {
        if (isMapOrList(descriptor))
            return fallback()
        //println("decode: d=${descriptor}, e=${descriptor.getElementDescriptor(index)}")
        val codec = codecOf<T>(descriptor, index) ?: return fallback()
        // IDK if user specified wrong codec. But i can check the returned value
        @Suppress("USELESS_CAST")
        return codec.read(input, null) as T
    }

    override fun decodeSequentially() = true

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>
    ): T {
        //println("decodeSerializableElement: d=${descriptor}")
        return if (deserializer.descriptor.kind is PrimitiveKind) {
            @Suppress("UNCHECKED_CAST")
            when (deserializer.descriptor.kind) {
                PrimitiveKind.BOOLEAN -> decodeBooleanElement(descriptor, index) as T
                PrimitiveKind.BYTE -> decodeByteElement(descriptor, index) as T
                PrimitiveKind.SHORT -> decodeShortElement(descriptor, index) as T
                PrimitiveKind.INT -> decodeIntElement(descriptor, index) as T
                PrimitiveKind.LONG -> decodeLongElement(descriptor, index) as T
                PrimitiveKind.FLOAT -> decodeFloatElement(descriptor, index) as T
                PrimitiveKind.DOUBLE -> decodeDoubleElement(descriptor, index) as T
                PrimitiveKind.CHAR -> decodeCharElement(descriptor, index) as T
                PrimitiveKind.STRING -> decodeCharElement(descriptor, index) as T
                else -> deserializer.deserialize(this)
            }
        } else
            deserializer.deserialize(this)
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readShort(input)
        }

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readString(input)
        }

    override fun decodeUnitElement(descriptor: SerialDescriptor, index: Int) = Unit

    override fun <T : Any> updateNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        old: T?
    ): T? {
        return deserializer.patch(this, old)
    }

    override fun <T> updateSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        old: T
    ): T {
        return deserializer.patch(this, old)
    }

    override fun decodeBoolean() = readBoolean(input)
    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readBoolean(input)
        }
    override fun decodeByte() = readByte(input)
    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readByte(input)
        }
    override fun decodeChar() = readShort(input).toChar()
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readShort(input).toChar()
        }
    override fun decodeDouble() = readDouble(input)
    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readDouble(input)
        }
    override fun decodeEnum(enumDescriptor: SerialDescriptor) = readVarInt(input)
    override fun decodeFloat() = readFloat(input)
    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readFloat(input)
        }
    override fun decodeInt() = readVarInt(input)
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int) = decode(descriptor, index) {
        readVarInt(input)
    }
    override fun decodeLong() = readVarLong(input)
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int) =
        decode(descriptor, index) {
            readVarLong(input)
        }

    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>
    ): T? {
        return decodeSerializableElement(descriptor, index, deserializer)
    }

    override fun decodeNotNullMark() = false
    override fun decodeNull() = throw SerializationException("null values are prohibited")
    override fun decodeShort() = readShort(input)
    override fun decodeString() = readString(input)
    override fun decodeUnit() = Unit

    override fun beginStructure(descriptor: SerialDescriptor, vararg typeParams: KSerializer<*>): CompositeDecoder {
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {}

    override fun decodeCollectionSize(descriptor: SerialDescriptor) = readVarInt(input)

    override fun decodeElementIndex(descriptor: SerialDescriptor): Nothing {
        throw SerializationException("Only sequential serialization mode is supported right now")
    }
}

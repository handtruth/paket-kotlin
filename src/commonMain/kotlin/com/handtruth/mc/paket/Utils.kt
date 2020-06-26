package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.Codecs
import com.handtruth.mc.paket.util.Path
import kotlinx.io.*
import kotlinx.io.text.readUtf8String
import kotlinx.io.text.writeUtf8String
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.StructureKind
import kotlin.reflect.KClass

internal fun sizeVarInt(value: Int): Int {
    var integer = value
    var count = 0
    do {
        integer = integer ushr 7
        count++
    } while (integer != 0)
    return count
}

internal fun readVarInt(input: Input): Int {
    var numRead = 0
    var result = 0
    var read: Int
    do {
        read = input.readByte().toInt()
        val value = read and 127
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 5) {
            throw RuntimeException("VarInt is too big")
        }
    } while (read and 128 != 0)
    return result
}

internal fun writeVarInt(output: Output, integer: Int) {
    var value = integer
    do {
        var temp = (value and 127)
        value = value ushr 7
        if (value != 0) {
            temp = temp or 128
        }
        output.writeByte(temp.toByte())
    } while (value != 0)
}

internal fun sizeVarLong(value: Long): Int {
    var integer = value
    var count = 0
    do {
        integer = integer ushr 7
        count++
    } while (integer != 0L)
    return count
}

internal fun readVarLong(input: Input): Long {
    var numRead = 0
    var result = 0L
    var read: Long
    do {
        read = input.readByte().toLong()
        val value = read and 127L
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 10) {
            throw RuntimeException("VarLong is too big")
        }
    } while (read and 128L != 0L)
    return result
}

internal fun writeVarLong(output: Output, integer: Long) {
    var value = integer
    do {
        var temp = (value and 127)
        value = value ushr 7
        if (value != 0L) {
            temp = temp or 128
        }
        output.writeByte(temp.toByte())
    } while (value != 0L)
}

private val Char.isHighSurrogate get() = this >= Char.MIN_HIGH_SURROGATE && this < (Char.MAX_HIGH_SURROGATE + 1)

internal fun sizeStringChars(sequence: CharSequence): Int {
    var count = 0
    var i = 0
    val len = sequence.length
    while (i < len) {
        val ch = sequence[i]
        when {
            ch.toInt() <= 0x7F -> count++
            ch.toInt() <= 0x7FF -> count += 2
            ch.isHighSurrogate -> {
                count += 4
                ++i
            }
            else -> count += 3
        }
        i++
    }
    return count
}

internal fun sizeString(sequence: CharSequence) = sizeStringChars(sequence).let { it + sizeVarInt(it) }

internal fun readString(input: Input): String {
    val size = readVarInt(input)
    val bytes = buildBytes {
        repeat(size) {
            // TODO: Improve when fixed
            writeByte(input.readByte())
        }
    }
    return bytes.input().readUtf8String()
}

internal fun writeString(output: Output, value: String) {
    writeVarInt(output, sizeStringChars(value))
    output.writeUtf8String(value)
}

internal const val sizeBoolean = Byte.SIZE_BYTES

internal fun readBoolean(input: Input) = input.readByte().toInt() != 0

internal fun writeBoolean(output: Output, value: Boolean) {
    output.writeByte(if (value) 1 else 0)
}

internal const val sizeByte = Byte.SIZE_BYTES

internal fun readByte(input: Input) = input.readByte()

internal fun writeByte(output: Output, value: Byte) = output.writeByte(value)

internal const val sizeShort = Short.SIZE_BYTES

internal fun readShort(input: Input): Short = input.readShort()

internal fun writeShort(output: Output, value: Short) = output.writeShort(value)

internal const val sizeLong = 8

internal fun readLong(input: Input) = input.readLong()

internal fun writeLong(output: Output, value: Long) = output.writeLong(value)

@ExperimentalPaketApi
internal fun sizePath(path: Path) = path.sumBy { sizeString(it) } + 1

@ExperimentalPaketApi
internal fun readPath(input: Input): Path {
    val result = mutableListOf<String>()
    do {
        val part = readString(input)
        if (part.isEmpty())
            break
        result += part
    } while (true)
    return Path(result)
}

@ExperimentalPaketApi
internal fun writePath(output: Output, value: Path) {
    for (segment in value)
        writeString(output, segment)
    writeByte(output, 0)
}

internal fun sizeByteArray(value: ByteArray) = value.size.let { sizeVarInt(it) + it }

internal fun readByteArray(input: Input): ByteArray {
    val size = readVarInt(input)
    val data = ByteArray(size)
    // TODO: Change when that bug will be fixed
    for (i in data.indices) {
        data[i] = input.readByte()
    }
    return data
}

internal fun writeByteArray(output: Output, value: ByteArray) {
    val size = value.size
    writeVarInt(output, size)
    // TODO: Change when that bug will be fixed
    value.forEach { output.writeByte(it) }
}

internal const val sizeInt = Int.SIZE_BYTES

internal fun readInt(input: Input): Int = input.readInt()

internal fun writeInt(output: Output, value: Int) = output.writeInt(value)

internal const val sizeFloat = Int.SIZE_BYTES

internal fun readFloat(input: Input) = input.readFloat()

internal fun writeFloat(output: Output, value: Float) = output.writeFloat(value)

internal const val sizeDouble = Long.SIZE_BYTES

internal fun readDouble(input: Input) = input.readDouble()

internal fun writeDouble(output: Output, value: Double) = output.writeDouble(value)

internal fun sizeBytes(bytes: Bytes) = bytes.size().let { it + sizeVarInt(it) }

internal fun readBytes(input: Input) = buildBytes {
    val size = readVarInt(input)
    // TODO: Optimize when fixed
    repeat(size) {
        writeByte(input.readByte())
    }
}

internal fun writeBytes(output: Output, bytes: Bytes) {
    writeVarInt(output, bytes.size())
    bytes.input().copyTo(output)
}

internal fun forbidNulls(): Nothing = throw SerializationException("null values a prohibited")

internal fun <T> codecOf(descriptor: SerialDescriptor, index: Int) =
    (descriptor.getElementAnnotations(index).find { it is WithCodec } as? WithCodec)?.let {
        @Suppress("UNCHECKED_CAST")
        Codecs.getInstance(it.codec as KClass<out Codec<T>>)
    }

internal fun isMapOrList(descriptor: SerialDescriptor) = descriptor.kind.let {
    it == StructureKind.LIST || it == StructureKind.MAP
}

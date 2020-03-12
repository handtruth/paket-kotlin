package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.Path
import kotlinx.io.*
import kotlinx.io.text.readUtf8String
import kotlinx.io.text.writeUtf8String

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

internal fun sizeStringChars(sequence: CharSequence): Int {
    var count = 0
    var i = 0
    val len = sequence.length
    while (i < len) {
        val ch = sequence[i]
        when {
            ch.toInt() <= 0x7F -> count++
            ch.toInt() <= 0x7FF -> count += 2
            Character.isHighSurrogate(ch) -> {
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
    val array = ByteArray(size)
    for (i in array.indices)
        array[i] = input.readByte()
    return String(array)
}

internal fun writeString(output: Output, value: String) {
    val bytes = value.toByteArray()
    writeVarInt(output, bytes.size)
    bytes.forEach { output.writeByte(it) }
}

internal const val sizeBoolean = 1

internal fun readBoolean(input: Input) = input.readByte().toInt() != 0

internal fun writeBoolean(output: Output, value: Boolean) {
    output.writeByte(if (value) 1 else 0)
}

internal const val sizeByte = 1

internal fun readByte(input: Input) = input.readByte()

internal fun writeByte(output: Output, value: Byte) = output.writeByte(value)

internal const val sizeShort = 2

internal fun readShort(input: Input): Short = input.readShort()

internal fun writeShort(output: Output, value: Short) = output.writeShort(value)

internal const val sizeLong = 8

internal fun readLong(input: Input) = input.readLong()

internal fun writeLong(output: Output, value: Long) = output.writeLong(value)

internal fun sizePath(path: Path) = path.sumBy { sizeString(it) } + 1

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

internal fun writePath(output: Output, value: Path) {
    for (segment in value)
        writeString(output, segment)
    writeByte(output, 0)
}

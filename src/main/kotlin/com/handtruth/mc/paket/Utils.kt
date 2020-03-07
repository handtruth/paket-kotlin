package com.handtruth.mc.paket

import java.io.File
import java.nio.file.Path

internal fun sizeVarInt(value: Int): Int {
    var integer = value
    var count = 0
    do {
        integer = integer ushr 7
        count++
    } while (integer != 0)
    return count
}

internal inline fun readVarInt(gather: (ByteArray) -> Unit): Int {
    var numRead = 0
    var result = 0
    var read: Int
    val bytes = ByteArray(1)
    do {
        gather(bytes)
        read = bytes[0].toInt()
        val value = read and 127
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 5) {
            throw RuntimeException("VarInt is too big")
        }
    } while (read and 128 != 0)
    return result
}

internal fun readVarInt(stream: AsyncInputStream) = readVarInt(stream::read)
internal suspend fun readVarIntAsync(stream: AsyncInputStream) = readVarInt { stream.readAsync(it) }

internal inline fun writeVarInt(integer: Int, gather: (ByteArray, Int, Int) -> Unit) {
    var value = integer
    val bytes = ByteArray(5)
    var count = 0
    do {
        var temp = (value and 127)
        value = value ushr 7
        if (value != 0) {
            temp = temp or 128
        }
        bytes[count++] = temp.toByte()
    } while (value != 0)
    gather(bytes, 0, count)
}

internal fun writeVarInt(stream: AsyncOutputStream, integer: Int) = writeVarInt(integer, stream::write)
internal suspend fun writeVarIntAsync(stream: AsyncOutputStream, integer: Int) =
    writeVarInt(integer) { a, b, c -> stream.writeAsync(a, b, c) }

internal fun sizeVarLong(value: Long): Int {
    var integer = value
    var count = 0
    do {
        integer = integer ushr 7
        count++
    } while (integer != 0L)
    return count
}

internal inline fun readVarLong(gather: (ByteArray) -> Unit): Long {
    var numRead = 0
    var result = 0L
    var read: Long
    val bytes = ByteArray(1)
    do {
        gather(bytes)
        read = bytes[0].toLong()
        val value = read and 127L
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 10) {
            throw RuntimeException("VarLong is too big")
        }
    } while (read and 128L != 0L)
    return result
}

internal fun readVarLong(stream: AsyncInputStream) = readVarLong(stream::read)
internal suspend fun readVarLongAsync(stream: AsyncInputStream) = readVarLong { stream.readAsync(it) }

internal inline fun writeVarLong(integer: Long, gather: (ByteArray, Int, Int) -> Unit) {
    var value = integer
    val bytes = ByteArray(10)
    var count = 0
    do {
        var temp = (value and 127)
        value = value ushr 7
        if (value != 0L) {
            temp = temp or 128
        }
        bytes[count++] = temp.toByte()
    } while (value != 0L)
    gather(bytes, 0, count)
}

internal fun writeVarLong(stream: AsyncOutputStream, integer: Long) = writeVarLong(integer, stream::write)
internal suspend fun writeVarLongAsync(stream: AsyncOutputStream, integer: Long) =
    writeVarLong(integer) { a, b, c -> stream.writeAsync(a, b, c) }

internal fun sizeString(sequence: CharSequence): Int {
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
    return count + sizeVarInt(count)
}

internal fun readString(stream: AsyncInputStream): String {
    val size = readVarInt(stream)
    val bytes = ByteArray(size)
    stream.read(bytes)
    return String(bytes, Charsets.UTF_8)
}

internal suspend fun readStringAsync(stream: AsyncInputStream): String {
    val size = readVarIntAsync(stream)
    val bytes = ByteArray(size)
    stream.readAsync(bytes)
    return String(bytes, Charsets.UTF_8)
}

internal fun writeString(stream: AsyncOutputStream, value: String) {
    val bytes = value.toByteArray(Charsets.UTF_8)
    writeVarInt(stream, bytes.size)
    stream.write(bytes)
}

internal suspend fun writeStringAsync(stream: AsyncOutputStream, value: String) {
    val bytes = value.toByteArray(Charsets.UTF_8)
    writeVarIntAsync(stream, bytes.size)
    stream.writeAsync(bytes)
}

internal const val sizeBoolean = 1

internal fun readBoolean(stream: AsyncInputStream): Boolean {
    val bytes = ByteArray(1)
    stream.read(bytes)
    return bytes[0] != 0.toByte()
}

internal suspend fun readBooleanAsync(stream: AsyncInputStream): Boolean {
    val bytes = ByteArray(1)
    stream.readAsync(bytes)
    return bytes[0] != 0.toByte()
}

internal fun writeBoolean(stream: AsyncOutputStream, value: Boolean) {
    stream.write(byteArrayOf(if (value) 1 else 0))
}

internal suspend fun writeBooleanAsync(stream: AsyncOutputStream, value: Boolean) {
    stream.writeAsync(byteArrayOf(if (value) 1 else 0))
}

internal const val sizeByte = 1

internal fun readByte(stream: AsyncInputStream): Byte {
    val bytes = ByteArray(1)
    stream.read(bytes)
    return bytes[0]
}

internal suspend fun readByteAsync(stream: AsyncInputStream): Byte {
    val bytes = ByteArray(1)
    stream.readAsync(bytes)
    return bytes[0]
}

internal fun writeByte(stream: AsyncOutputStream, value: Byte) = stream.write(byteArrayOf(value))

internal suspend fun writeByteAsync(stream: AsyncOutputStream, value: Byte) = stream.writeAsync(byteArrayOf(value))

internal const val sizeShort = 2

internal fun readShort(stream: AsyncInputStream): Int {
    val bytes = ByteArray(2)
    stream.read(bytes)
    return ((bytes[0].toInt() and 255 shl 8) or (bytes[1].toInt() and 255))
}

internal suspend fun readShortAsync(stream: AsyncInputStream): Int {
    val bytes = ByteArray(2)
    stream.readAsync(bytes)
    return ((bytes[0].toInt() and 255 shl 8) or (bytes[1].toInt() and 255))
}

internal fun writeShort(stream: AsyncOutputStream, value: Int) {
    stream.write(byteArrayOf(((value ushr 8) and 255).toByte(), (value and 255).toByte()))
}

internal suspend fun writeShortAsync(stream: AsyncOutputStream, value: Int) {
    stream.writeAsync(byteArrayOf(((value ushr 8) and 255).toByte(), (value and 255).toByte()))
}

internal const val sizeLong = 8

internal fun readLong(stream: AsyncInputStream): Long {
    val data = ByteArray(8)
    stream.read(data)
    var result = 0L
    for (i in 0..7)
        result = result or (data[i].toLong() and 255 shl (i shl 3))
    return result
}

internal suspend fun readLongAsync(stream: AsyncInputStream): Long {
    val data = ByteArray(8)
    stream.readAsync(data)
    var result = 0L
    for (i in 0..7)
        result = result or (data[i].toLong() and 255 shl (i shl 3))
    return result
}

internal fun writeLong(stream: AsyncOutputStream, value: Long) {
    stream.write(ByteArray(8) { ((value ushr (it shl 3)) and 255).toByte() })
}

internal suspend fun writeLongAsync(stream: AsyncOutputStream, value: Long) {
    stream.writeAsync(ByteArray(8) { ((value ushr (it shl 3)) and 255).toByte() })
}

internal val CharSequence.pathParts get() = split('/')
    .asSequence()
    .filter { it.isNotEmpty() }

internal fun sizePath(path: CharSequence) = if (path == "/") 3 else
    ((if (path.startsWith('/')) 1 else 0) + path.pathParts.sumBy { sizeString(it) } + sizeByte)

internal inline fun readPath(reader: () -> CharSequence): String {
    val first = reader()
    if (first.isEmpty())
        return ""
    val result = StringBuilder()
    result.append(first)
    do {
        val part = reader()
        if (part.isEmpty())
            break
        result.append('/').append(part)
    } while (true)
    return result.toString()
}

internal fun readPath(stream: AsyncInputStream) = readPath { readString(stream) }
internal suspend fun readPathAsync(stream: AsyncInputStream) = readPath { readStringAsync(stream) }

internal inline fun writePath(value: String, writer: (String) -> Unit) {
    val iter = value.pathParts.iterator()
    if (!iter.hasNext()) {
        if (value.startsWith('/'))
            writer("/")
        writer("")
        return
    }
    val first = iter.next()
    if (value.startsWith('/'))
        writer("/$first")
    else
        writer(first)
    while (iter.hasNext())
        writer(iter.next())
    writer("")
}

internal fun writePath(stream: AsyncOutputStream, value: String) = writePath(value) { writeString(stream, it) }
internal suspend fun writePathAsync(stream: AsyncOutputStream, value: String) =
    writePath(value) { writeStringAsync(stream, it) }

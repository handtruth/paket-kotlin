package com.handtruth.mc.paket

import java.io.Closeable

interface AsyncInputStream : Closeable {
    fun read(bytes: ByteArray, offset: Int, length: Int, timeout: Long)
    fun read(bytes: ByteArray, offset: Int, length: Int) = read(bytes, offset, length, -1)
    fun read(bytes: ByteArray, timeout: Long) = read(bytes, 0, bytes.size, timeout)
    fun read(bytes: ByteArray) = read(bytes, -1)
    suspend fun readAsync(bytes: ByteArray, offset: Int, length: Int)
    suspend fun readAsync(bytes: ByteArray) = readAsync(bytes, 0, bytes.size)
}

interface AsyncOutputStream : Closeable {
    fun write(bytes: ByteArray, offset: Int, length: Int, timeout: Long)
    fun write(bytes: ByteArray, offset: Int, length: Int) = write(bytes, offset, length, -1)
    fun write(bytes: ByteArray, timeout: Long) = write(bytes, 0, bytes.size, timeout)
    fun write(bytes: ByteArray) = write(bytes, -1)
    suspend fun writeAsync(bytes: ByteArray, offset: Int, length: Int)
    suspend fun writeAsync(bytes: ByteArray) = writeAsync(bytes, 0, bytes.size)
}

interface AsyncStream : AsyncInputStream, AsyncOutputStream

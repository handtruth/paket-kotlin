package com.handtruth.mc.paket

import java.io.Closeable

interface AsyncInputStream : Closeable {
    fun read(bytes: ByteArray, offset: Int, length: Int, timeout: Long = 0)
    fun read(bytes: ByteArray, timeout: Long = 0) = read(bytes, 0, bytes.size, timeout)
    suspend fun readAsync(bytes: ByteArray, offset: Int, length: Int)
    suspend fun readAsync(bytes: ByteArray) = readAsync(bytes, 0, bytes.size)
}

interface AsyncOutputStream : Closeable {
    fun write(bytes: ByteArray, offset: Int, length: Int, timeout: Long = 0)
    fun write(bytes: ByteArray, timeout: Long = 0) = write(bytes, 0, bytes.size, timeout)
    suspend fun writeAsync(bytes: ByteArray, offset: Int, length: Int)
    suspend fun writeAsync(bytes: ByteArray) = writeAsync(bytes, 0, bytes.size)
}

interface AsyncStream : AsyncInputStream, AsyncOutputStream

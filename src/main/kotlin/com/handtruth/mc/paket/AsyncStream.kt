package com.handtruth.mc.paket

import java.io.Closeable

interface AsyncInputStream : Closeable {
    fun read(bytes: ByteArray, offset: Int, length: Int)
    fun read(bytes: ByteArray) = read(bytes, 0, bytes.size)
    suspend fun readAsync(bytes: ByteArray, offset: Int, length: Int)
    suspend fun readAsync(bytes: ByteArray) = readAsync(bytes, 0, bytes.size)
}

interface AsyncOutputStream : Closeable {
    fun write(bytes: ByteArray, offset: Int, length: Int)
    fun write(bytes: ByteArray) = write(bytes, 0, bytes.size)
    suspend fun writeAsync(bytes: ByteArray, offset: Int, length: Int)
    suspend fun writeAsync(bytes: ByteArray) = writeAsync(bytes, 0, bytes.size)
}

interface AsyncStream : AsyncInputStream, AsyncOutputStream

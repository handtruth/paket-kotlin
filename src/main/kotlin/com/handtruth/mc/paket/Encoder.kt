package com.handtruth.mc.paket

interface Encoder<T> {
    fun measure(value: T)
    fun read(stream: AsyncInputStream): T
    suspend fun readAsync(stream: AsyncInputStream): T
    fun write(stream: AsyncOutputStream, value: T)
    suspend fun writeAsync(stream: AsyncOutputStream, value: T)
}

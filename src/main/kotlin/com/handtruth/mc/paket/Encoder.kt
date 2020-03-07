package com.handtruth.mc.paket

interface Encoder<T> {
    fun measure(value: T): Int
    fun read(stream: AsyncInputStream, old: T?): T
    suspend fun readAsync(stream: AsyncInputStream, old: T?): T
    fun write(stream: AsyncOutputStream, value: T)
    suspend fun writeAsync(stream: AsyncOutputStream, value: T)
}

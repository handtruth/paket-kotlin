package com.handtruth.mc.paket

import java.io.InputStream
import java.io.OutputStream

class JavaInputStream(private val stream: InputStream) : AsyncInputStream {
    override fun read(bytes: ByteArray, offset: Int, length: Int, timeout: Long) {
        var readCount = 0
        while (readCount < length)
            readCount += stream.read(bytes, offset + readCount, length - readCount)
    }
    override suspend fun readAsync(bytes: ByteArray, offset: Int, length: Int) = throw UnsupportedOperationException()
    override fun close() = stream.close()
}

class JavaOutputStream(private val stream: OutputStream) : AsyncOutputStream {
    override fun write(bytes: ByteArray, offset: Int, length: Int, timeout: Long)  = stream.write(bytes, offset, length)
    override suspend fun writeAsync(bytes: ByteArray, offset: Int, length: Int) = throw UnsupportedOperationException()
    override fun close() = stream.close()
}

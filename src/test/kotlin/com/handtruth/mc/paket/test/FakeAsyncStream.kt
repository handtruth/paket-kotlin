package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.AsyncInputStream
import com.handtruth.mc.paket.AsyncOutputStream
import java.io.InputStream
import java.io.OutputStream

class FakeAsyncStream(private val input: InputStream?,
                      private val output: OutputStream?) : AsyncInputStream, AsyncOutputStream {
    constructor(input: InputStream) : this(input, null)
    constructor(output: OutputStream) : this(null, output)

    override fun read(bytes: ByteArray, offset: Int, length: Int) {
        input!!
        var received = 0
        while (received != length)
            received += input.read(bytes, received + offset, length - received)
    }

    override suspend fun readAsync(bytes: ByteArray, offset: Int, length: Int) =
        read(bytes, offset, length)

    override fun write(bytes: ByteArray, offset: Int, length: Int) =
        output!!.write(bytes, offset, length)

    override suspend fun writeAsync(bytes: ByteArray, offset: Int, length: Int) =
        write(bytes, offset, length)

    override fun close() {
        input?.close()
        output?.close()
    }
}

package com.handtruth.mc.paket

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.full.primaryConstructor

open class PaketTransmitter(private val input: AsyncInputStream,
                            private val output: AsyncOutputStream): Closeable {

    companion object {
        fun create(input: InputStream, output: OutputStream) =
            PaketTransmitter(JavaInputStream(input), JavaOutputStream(output))
    }

    private var privateId = -1
    val idOrdinal get() = privateId
    private var privateSize = -1
    val size get() = privateSize
    private var idSz = -1

    private var isCaught = false

    private val counterInput = CounterAsyncInputStream(input)

    fun catchOrdinal(): Int = if (isCaught) {
        drop()
        catchOrdinal()
    } else {
        privateSize = readVarInt(input)
        counterInput.clear()
        privateId = readVarInt(counterInput)
        idSz = counterInput.amount
        isCaught = true
        privateId
    }

    suspend fun catchOrdinalAsync(): Int = if (isCaught) {
        dropAsync()
        catchOrdinalAsync()
    } else {
        privateSize = readVarIntAsync(input)
        counterInput.clear()
        privateId = readVarIntAsync(counterInput)
        idSz = counterInput.amount
        isCaught = true
        privateId
    }

    fun drop() {
        if (isCaught) {
            val toRead = privateSize - idSz
            if (toRead < 0)
                throw IllegalProtocolStateException("can't drop on illegal state")
            input.read(ByteArray(toRead))
            isCaught = false
        } else {
            catchOrdinal()
            drop()
        }
    }

    suspend fun dropAsync() {
        if (isCaught) {
            val toRead = privateSize - idSz
            if (toRead < 0)
                throw IllegalProtocolStateException("can't drop on illegal state")
            input.readAsync(ByteArray(toRead))
            isCaught = false
        } else {
            catchOrdinalAsync()
            dropAsync()
        }
    }

    fun read(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        if (privateId != paket.id.ordinal)
            throw IllegalProtocolStateException("Paket IDs differ (${paket.id.ordinal} expected, got $privateId)")
        for (field in paket.fields)
            field.read(counterInput)
        isCaught = false
        if (privateSize != counterInput.amount)
            throw IllegalProtocolStateException("Paket sizes differ ($privateSize expected, got ${counterInput.amount})")
    }

    suspend fun readAsync(paket: Paket) {
        if (!isCaught)
            catchOrdinalAsync()
        if (privateId != paket.id.ordinal)
            throw IllegalProtocolStateException("Paket IDs differ (${paket.id.ordinal} expected, got $privateId)")
        for (field in paket.fields)
            field.readAsync(counterInput)
        isCaught = false
        if (privateSize != counterInput.amount)
            throw IllegalProtocolStateException("Paket sizes differ ($privateSize expected, got ${counterInput.amount})")
    }

    inline fun <reified T: Paket> read(): T {
        val paket = T::class.primaryConstructor!!.callBy(emptyMap())
        read(paket)
        return paket
    }

    suspend inline fun <reified T: Paket> readAsync(): T {
        val paket = T::class.primaryConstructor!!.callBy(emptyMap())
        readAsync(paket)
        return paket
    }

    fun write(paket: Paket) {
        writeVarInt(output, paket.size)
        writeVarInt(output, paket.id.ordinal)
        for (field in paket.fields)
            field.write(output)
    }

    suspend fun writeAsync(paket: Paket) {
        writeVarIntAsync(output, paket.size)
        writeVarIntAsync(output, paket.id.ordinal)
        for (field in paket.fields)
            field.writeAsync(output)
    }

    private class CounterAsyncInputStream(private val parent: AsyncInputStream) : AsyncInputStream {
        override fun close() = parent.close()
        override fun read(bytes: ByteArray, offset: Int, length: Int) {
            parent.read(bytes, offset, length)
            counter += length
        }

        override suspend fun readAsync(bytes: ByteArray, offset: Int, length: Int) {
            parent.readAsync(bytes, offset, length)
            counter += length
        }

        private var counter = 0
        val amount get() = counter
        fun clear() { counter = 0 }
    }

    override fun close() {
        counterInput.close()
        output.close()
    }
}
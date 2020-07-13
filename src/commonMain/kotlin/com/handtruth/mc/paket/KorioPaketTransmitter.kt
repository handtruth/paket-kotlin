@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import com.handtruth.mc.paket.fields.VarIntCodec
import com.soywiz.korio.stream.*
import kotlinx.io.*
import kotlin.math.min

private class KorioPaketSender(private val stream: AsyncOutputStream) : AbstractPaketSender() {
    override suspend fun send(paket: Paket) {
        val output = ByteArrayOutput()
        paket.write(output)
        val array = output.toByteArray()
        val sizeOutput = ByteArrayOutput(5)
        VarIntCodec.write(sizeOutput, array.size)
        stream.write(sizeOutput.toByteArray(), 0, VarIntCodec.measure(array.size))
        stream.write(array, 0, array.size)
    }
}

fun PaketSender(output: AsyncOutputStream): PaketSender = KorioPaketSender(output)

private class KorioPaketReceiver(private val stream: AsyncInputStream) : AbstractPaketReceiver() {
    private val pending = ByteArray(5)
    private var pendingOffset = 0

    override suspend fun catchOrdinal(): Int = breakableAction {
        if (isCaught) {
            drop()
            catchOrdinal()
        } else {
            // Read size
            for (i in 0..4) {
                val byte = stream.read()
                pending[i] = byte.toByte()
                if (byte and 0x80 == 0)
                    break
            }
            val inputSize = ByteArrayInput(pending)
            size = VarIntCodec.read(inputSize, null)
            // Read ID
            stream.readExact(pending, 0, min(size, 5))
            val inputId = ByteArrayInput(pending)
            val id = VarIntCodec.read(inputId, null)
            pendingOffset = VarIntCodec.measure(id)
            isCaught = true
            idOrdinal = id
            // Fetch paket body
            val alreadyRead = min(5, size)
            val packet = stream.readBytesExact(size - alreadyRead)
            val bytes = buildBytes {
                // TODO: Improve when fixed
                for (i in 0 until alreadyRead)
                    writeByte(pending[i])
                packet.forEach { writeByte(it) }
            }
            buffer = BytesInfo(bytes)

            id
        }
    }

    private class BytesInfo(val bytes: Bytes) : Closeable {
        val input = bytes.input()

        override fun close() {
            input.close()
            bytes.close()
        }
    }

    private var buffer: BytesInfo? = null

    override suspend fun drop(): Unit = breakableAction {
        if (isCaught) {
            buffer!!.close()
            isCaught = false
        } else {
            catchOrdinal()
            drop()
        }
    }

    override suspend fun receive(paket: Paket) = breakableAction {
        if (!isCaught)
            catchOrdinal()
        paket.read(buffer!!.input)
        drop()
    }

    override suspend fun peek(paket: Paket) = breakableAction {
        if (!isCaught)
            catchOrdinal()
        buffer!!.input.preview {
            paket.read(this)
        }
    }
}

fun PaketReceiver(input: AsyncInputStream): PaketReceiver = KorioPaketReceiver(input)

fun PaketTransmitter(input: AsyncInputStream, output: AsyncOutputStream) =
    PaketTransmitter(PaketReceiver(input), PaketSender(output))
fun PaketTransmitter(stream: AsyncStream) = PaketTransmitter(stream, stream)

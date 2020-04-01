@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import com.handtruth.mc.paket.fields.VarIntCodec
import io.ktor.utils.io.*
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.io.buildBytes
import kotlin.math.min

private class KtorPaketSender(private val channel: ByteWriteChannel) : AbstractPaketSender() {

    override suspend fun send(paket: Paket) = breakableAction {
        val output = ByteArrayOutput()
        paket.write(output)
        val array = output.toByteArray()
        val sizeOutput = ByteArrayOutput(5)
        VarIntCodec.write(sizeOutput, array.size)
        channel.writeFully(sizeOutput.toByteArray(), 0, VarIntCodec.measure(array.size))
        channel.writeFully(array, 0, array.size)
        channel.flush()
    }

    override fun close() {
        super.close()
        channel.close(CancellationException("Paket sender closed"))
    }
}

fun PaketSender(channel: ByteWriteChannel): PaketSender = KtorPaketSender(channel)

private class KtorPaketReceiver(private val channel: ByteReadChannel) : AbstractPaketReceiver() {
    private val pending = ByteArray(5)
    private var pendingOffset = 0

    override suspend fun catchOrdinal(): Int = breakableAction {
        if (isCaught) {
            drop()
            catchOrdinal()
        } else {
            // Read size
            for (i in 0..4) {
                val byte = channel.readByte()
                pending[i] = byte
                if (byte.toInt() and 0x80 == 0)
                    break
            }
            val inputSize = ByteArrayInput(pending)
            size = VarIntCodec.read(inputSize, null)
            // Read ID
            channel.readFully(pending, 0, min(size, 5))
            val inputId = ByteArrayInput(pending)
            val id = VarIntCodec.read(inputId, null)
            pendingOffset = VarIntCodec.measure(id)
            isCaught = true
            idOrdinal = id
            id
        }
    }

    override suspend fun drop(): Unit = breakableAction {
        if (isCaught) {
            val size = size
            channel.discardExact(size.toLong() - min(size, 5))
            isCaught = false
        } else {
            catchOrdinal()
            drop()
        }
    }

    override suspend fun receive(paket: Paket) = breakableAction {
        if (!isCaught)
            catchOrdinal()
        val id = idOrdinal
        if (id != paket.id.ordinal)
            throw IllegalProtocolStateException("Paket IDs differ (${paket.id.ordinal} expected, got $id)")
        val alreadyRead = min(5, size)
        val packet = channel.readPacket(size - alreadyRead)
        val bytes = buildBytes {
            // TODO: Improve when fixed
            for (i in 0 until alreadyRead)
                writeByte(pending[i])
            while (packet.canRead())
                writeByte(packet.readByte())
        }
        paket.read(bytes.input())
        bytes.close()
        isCaught = false
    }

    override suspend fun peek(paket: Paket) {
        TODO("Not yet implemented")
    }
}

fun PaketReceiver(channel: ByteReadChannel): PaketReceiver = KtorPaketReceiver(channel)

fun PaketTransmitter(input: ByteReadChannel, output: ByteWriteChannel) =
    PaketTransmitter(PaketReceiver(input), PaketSender(output))
fun PaketTransmitter(channel: ByteChannel) = PaketTransmitter(channel, channel)

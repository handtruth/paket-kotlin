@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.io.Bytes
import kotlinx.io.buildBytes
import kotlinx.io.use

private class BytesPaketSender(private val output: SendChannel<Bytes>) : PaketSender {

    override suspend fun send(paket: Paket) {
        val data = buildBytes {
            paket.write(this)
        }
        output.send(data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val broken
        get() = output.isClosedForSend

    override fun close() {
        output.close()
    }
}

private class BytesPaketReceiver(private val input: ReceiveChannel<Bytes>) : AbstractPaketReceiver() {

    private class BytesInfo(bytes: Bytes) {
        val input = bytes.input()
        val size = bytes.size()
    }

    private var pending: BytesInfo? = null

    override suspend fun catchOrdinal(): Int = breakableAction {
        val buffer = BytesInfo(input.receive())
        pending = buffer
        buffer.input.preview {
            idOrdinal = readVarInt(this)
        }
        size = buffer.size
        isCaught = true
        return idOrdinal
    }

    private fun clear() {
        pending = null
        isCaught = false
        size = -1
        idOrdinal = -1
    }

    override suspend fun drop() = breakableAction {
        if (!isCaught)
            catchOrdinal()
        clear()
    }

    override suspend fun receive(paket: Paket) = breakableAction {
        if (!isCaught)
            catchOrdinal()
        pending!!.input.use { paket.read(it) }
        clear()
    }

    override suspend fun peek(paket: Paket) = breakableAction {
        if (!isCaught)
            catchOrdinal()
        pending!!.input.preview { paket.read(this) }
    }

    override fun close() {
        input.cancel()
        super.close()
    }

}

@ExperimentalPaketApi
fun PaketSender(output: SendChannel<Bytes>): PaketSender = BytesPaketSender(output)

@ExperimentalPaketApi
fun PaketReceiver(input: ReceiveChannel<Bytes>): PaketReceiver = BytesPaketReceiver(input)

@ExperimentalPaketApi
fun PaketTransmitter(input: ReceiveChannel<Bytes>, output: SendChannel<Bytes>): PaketTransmitter =
    PaketTransmitter(PaketReceiver(input), PaketSender(output))

@ExperimentalPaketApi
fun PaketTransmitter(channel: Channel<Bytes>): PaketTransmitter =
    PaketTransmitter(channel, channel)

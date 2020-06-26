@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

private class ChannelPaketSender(val output: SendChannel<Paket>) : PaketSender {

    override suspend fun send(paket: Paket) {
        output.send(paket)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val broken
        get() = output.isClosedForSend

    override fun close() {
        output.close()
    }

}

private class ChannelPaketReceiver(val input: ReceiveChannel<Paket>) : PaketReceiver {

    private var pending: Paket? = null

    override val idOrdinal get() = pending?.id?.ordinal ?: -1
    override val size get() = pending?.size ?: -1
    override val isCaught get() = pending != null

    override suspend fun catchOrdinal(): Int {
        val paket = input.receive()
        pending = paket
        return paket.id.ordinal
    }

    override suspend fun drop() {
        if (!isCaught)
            catchOrdinal()
        pending = null
    }

    override suspend fun receive(paket: Paket) {
        peek(paket)
        drop()
    }

    override suspend fun peek(paket: Paket) {
        if (!isCaught)
            catchOrdinal()
        val received = pending!!
        for ((a, b) in received.fields zip paket.fields) {
            @Suppress("UNCHECKED_CAST")
            b as Field<Any>
            @Suppress("UNCHECKED_CAST")
            a as Field<Any>
            b.value = a.value
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val broken
        get() = input.isClosedForReceive

    override fun close() {
        input.cancel()
    }

}

fun PaketSender(output: SendChannel<Paket>): PaketSender = ChannelPaketSender(output)
fun PaketReceiver(input: ReceiveChannel<Paket>): PaketReceiver = ChannelPaketReceiver(input)

fun PaketTransmitter(input: ReceiveChannel<Paket>, output: SendChannel<Paket>): PaketTransmitter =
    PaketTransmitter(PaketReceiver(input), PaketSender(output))

fun PaketTransmitter(channel: Channel<Paket>): PaketTransmitter =
    PaketTransmitter(channel, channel)

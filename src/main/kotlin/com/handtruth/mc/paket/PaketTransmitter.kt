@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.Dispatchers
import kotlinx.io.Closeable
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

interface PaketTransmitter : PaketSender, PaketReceiver

fun PaketTransmitter(receiver: PaketReceiver, sender: PaketSender): PaketTransmitter
    = CombinedPaketTransmitter(receiver, sender)

fun PaketTransmitter(input: Input, output: Output, ioContext: CoroutineContext = Dispatchers.IO) =
    PaketTransmitter(PaketReceiver(input, ioContext), PaketSender(output, ioContext))

private class CombinedPaketTransmitter(private val receiver: PaketReceiver, private val sender: PaketSender) :
        PaketTransmitter, PaketReceiver by receiver, PaketSender by sender {
    override val broken get() = receiver.broken && sender.broken

    override fun close() {
        receiver.close()
        sender.close()
    }
}

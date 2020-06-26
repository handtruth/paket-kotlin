@file:Suppress("FunctionName")

package com.handtruth.mc.paket

interface PaketTransmitter : PaketSender, PaketReceiver

fun PaketTransmitter(receiver: PaketReceiver, sender: PaketSender): PaketTransmitter
    = CombinedPaketTransmitter(receiver, sender)

private class CombinedPaketTransmitter(private val receiver: PaketReceiver, private val sender: PaketSender) :
        PaketTransmitter, PaketReceiver by receiver, PaketSender by sender {
    override val broken get() = receiver.broken && sender.broken

    override fun close() {
        receiver.close()
        sender.close()
    }
}

fun PaketTransmitter.asSynchronized() = PaketTransmitter(
    (this as PaketReceiver).asSynchronized(), (this as PaketSender).asSynchronized()
)

suspend inline fun <reified E: Enum<E>> PaketTransmitter.catchAs() = enumValues<E>()[catchOrdinal()]
inline fun <reified E: Enum<E>> PaketTransmitter.idAs() = enumValues<E>()[idOrdinal]

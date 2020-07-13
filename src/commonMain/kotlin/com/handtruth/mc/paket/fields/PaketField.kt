package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

class PaketCodec<P: Paket>(val source: PaketSource<P>) : Codec<P> {
    override fun measure(value: P) = value.size
    override fun read(input: Input, old: P?) = (old ?: source.produce()).apply { read(input) }
    override fun write(output: Output, value: P) = value.write(output)
}

class PaketListCodec<P: Paket>(source: PaketSource<P>) : ListCodec<P>(PaketCodec(source))

class PaketField<P: Paket>(initial: P, source: PaketSource<P>) : Field<P>(PaketCodec(source), initial)
class PaketListField<P: Paket>(initial: MutableList<P>, source: PaketSource<P>) :
    ListField<P>(PaketListCodec(source), initial)

fun <P : Paket> Paket.paket(source: PaketSource<P>, initial: P = source.produce()) = field(PaketField(initial, source))
fun <P : Paket> Paket.paket(initial: P) = field(PaketField(initial, emptyPaketSource()))
fun <P : Paket> Paket.listOfPaket(source: PaketSource<P>, initial: MutableList<P>) =
    field(PaketListField(initial, source))
@JvmName("listOfPaketRO")
fun <P: Paket> Paket.listOfPaket(source: PaketSource<P>, initial: List<P>) =
    listOfPaket(source, initial.toMutableList())

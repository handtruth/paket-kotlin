package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KClass

class PaketEncoder<P: Paket>(val `class`: KClass<P>) : Encoder<P> {
    override fun measure(value: P) = value.size
    override fun read(input: Input, old: P?) = (old ?: constructPaket(`class`)).apply { read(input) }
    override fun write(output: Output, value: P) = value.write(output)
}

class PaketListEncoder<P: Paket>(`class`: KClass<P>) : ListEncoder<P>(PaketEncoder(`class`))

class PaketField<P: Paket>(initial: P, `class`: KClass<P>) : Field<P>(PaketEncoder(`class`), initial)
class PaketListField<P: Paket>(initial: MutableList<P>, `class`: KClass<P>) :
    ListField<P>(PaketListEncoder(`class`), initial)

inline fun <reified P: Paket> Paket.paket(initial: P = constructPaket(P::class)) = field(PaketField(initial, P::class))
inline fun <reified P: Paket> Paket.listOfPaket(initial: MutableList<P>) = field(PaketListField(initial, P::class))
@JvmName("listOfPaketRO")
inline fun <reified P: Paket> Paket.listOfBool(initial: List<P>) = listOfPaket(initial.toMutableList())

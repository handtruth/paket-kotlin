package com.handtruth.mc.paket.fields

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.paket.*
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class NBTEncoder<T>(val serializer: KSerializer<T>) : Encoder<T> {
    override fun measure(value: T) = NBT.dump(serializer, value).size
    override fun read(input: Input, old: T?) = NBT.deserialize(serializer, input)
    override fun write(output: Output, value: T) = NBT.serialize(serializer, output, value)
}

class NBTListEncoder<T>(serializer: KSerializer<T>) : ListEncoder<T>(NBTEncoder(serializer))

class NBTField<T>(initial: T, serializer: KSerializer<T>) : Field<T>(NBTEncoder(serializer), initial)
class NBTListField<T>(initial: MutableList<T>, serializer: KSerializer<T>) :
    ListField<T>(NBTListEncoder(serializer), initial)

fun <T> Paket.nbt(initial: T, serializer: KSerializer<T>) = field(NBTField(initial, serializer))
inline fun <reified T: Any> Paket.nbt(initial: T) = nbt(initial, T::class.serializer())
fun <T> Paket.listOfNbt(initial: MutableList<T>, serializer: KSerializer<T>) =
    field(NBTListField(initial, serializer))
inline fun <reified T: Any> Paket.listOfNbt(initial: MutableList<T> = mutableListOf()) =
    listOfNbt(initial, T::class.serializer())
@JvmName("listOfNbtRO")
fun <T> Paket.listOfNbt(initial: List<T>, serializer: KSerializer<T>) = listOfNbt(initial.toMutableList(), serializer)
@JvmName("listOfNbtRO")
inline fun <reified T: Any> Paket.listOfNbt(initial: List<T>) = listOfNbt(initial.toMutableList(), T::class.serializer())

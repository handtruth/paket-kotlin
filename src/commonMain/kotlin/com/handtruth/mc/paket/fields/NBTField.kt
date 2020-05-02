package com.handtruth.mc.paket.fields

import com.handtruth.mc.nbt.NBT
import com.handtruth.mc.paket.Paket
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

fun <T> Paket.nbt(initial: T, serializer: KSerializer<T>) = binary(NBT.Default, initial, serializer)
inline fun <reified T: Any> Paket.nbt(initial: T) = nbt(initial, T::class.serializer())

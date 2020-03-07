package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*

object PathEncoder : Encoder<String> {
    override fun measure(value: String) = sizePath(value)
    override fun read(stream: AsyncInputStream, old: String?) = readPath(stream)
    override suspend fun readAsync(stream: AsyncInputStream, old: String?) = readPathAsync(stream)
    override fun write(stream: AsyncOutputStream, value: String) = writePath(stream, value)
    override suspend fun writeAsync(stream: AsyncOutputStream, value: String) = writePathAsync(stream, value)
}

object PathListEncoder : ListEncoder<String>(PathEncoder)

class PathField(initial: String) : Field<String>(PathEncoder, initial)
class PathListField(initial: MutableList<String>) : ListField<String>(PathListEncoder, initial)

fun Paket.path(initial: String = "") = field(PathField(initial))
fun Paket.listOfPath(initial: MutableList<String> = mutableListOf()) = field(PathListField(initial))
@JvmName("listOfPathRO")
fun Paket.listOfPath(initial: List<String>) = listOfPath(initial.toMutableList())

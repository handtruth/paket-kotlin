package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.util.Path
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.jvm.JvmName

@ExperimentalPaketApi
object PathCodec : Codec<Path> {
    override fun measure(value: Path) = sizePath(value)
    override fun read(input: Input, old: Path?) = readPath(input)
    override fun write(output: Output, value: Path) = writePath(output, value)
}

@ExperimentalPaketApi
object PathListCodec : ListCodec<Path>(PathCodec)

@ExperimentalPaketApi
class PathField(initial: Path) : Field<Path>(PathCodec, initial)
@ExperimentalPaketApi
class PathListField(initial: MutableList<Path>) : ListField<Path>(PathListCodec, initial)

@ExperimentalPaketApi
fun Paket.path(initial: Path = Path.empty) = field(PathField(initial))
@ExperimentalPaketApi
fun Paket.path(initial: String) = field(PathField(Path(initial)))
@ExperimentalPaketApi
fun Paket.listOfPath(initial: MutableList<Path> = mutableListOf()) = field(PathListField(initial))
@ExperimentalPaketApi @JvmName("listOfPathRO")
fun Paket.listOfPath(initial: List<Path>) = listOfPath(initial.toMutableList())

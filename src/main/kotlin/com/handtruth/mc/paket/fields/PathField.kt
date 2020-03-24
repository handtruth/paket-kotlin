package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.util.Path
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KProperty

object PathEncoder : Encoder<Path> {
    override fun measure(value: Path) = sizePath(value)
    override fun read(input: Input, old: Path?) = readPath(input)
    override fun write(output: Output, value: Path) = writePath(output, value)
}

object PathListEncoder : ListEncoder<Path>(PathEncoder)

class PathField(initial: Path) : Field<Path>(PathEncoder, initial)
class PathListField(initial: MutableList<Path>) : ListField<Path>(PathListEncoder, initial)

fun Paket.path(initial: Path = Path.empty) = field(PathField(initial))
fun Paket.path(initial: String) = field(PathField(Path(initial)))
fun Paket.listOfPath(initial: MutableList<Path> = mutableListOf()) = field(PathListField(initial))
@JvmName("listOfPathRO")
fun Paket.listOfPath(initial: List<Path>) = listOfPath(initial.toMutableList())

package com.handtruth.mc.paket

import com.handtruth.mc.paket.util.PaketDecoder
import com.handtruth.mc.paket.util.PaketEncoder
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.io.Input
import kotlinx.io.Output
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptyModule

abstract class Paket {
    abstract val id: Enum<*>

    private val mutableFields = mutableListOf<Field<*>>()
    val fields: List<Field<*>> get() = mutableFields

    val size get() = sizeVarInt(id.ordinal) + mutableFields.sumBy { it.size }

    override fun equals(other: Any?): Boolean {
        if (other is Paket && id == other.id && mutableFields.size == other.mutableFields.size) {
            for (i in 0 until mutableFields.size)
                if (mutableFields[i] != other.mutableFields[i])
                    return false
            return true
        }
        return false
    }
    override fun hashCode() = id.hashCode() + mutableFields.hashCode()
    override fun toString(): String {
        val builder = StringBuilder(id.toString()).append(":{ ")
        for (i in 0 until mutableFields.size - 1)
            builder.append(mutableFields[i].toString()).append("; ")
        if (mutableFields.isNotEmpty())
            builder.append(mutableFields.last().toString()).append(' ')
        builder.append('}')
        return builder.toString()
    }

    fun <F> field(field: Field<F>): Field<F> {
        mutableFields += field
        return field
    }

    fun write(output: Output) {
        writeVarInt(output, id.ordinal)
        for (field in fields)
            field.write(output)
    }

    fun read(input: Input) {
        val otherId = readVarInt(input)
        validate(id.ordinal == otherId) { "Wrong paket id (${id.ordinal} expected, got $otherId)" }
        for (field in fields)
            field.read(input)
    }

    open fun clear() {}

    private var pool: PaketPool<in Paket>? = null

    fun attachToPool(pool: PaketPool<in Paket>) {
        check(this.pool == null) { "Paket pool already specified for this paket" }
        this.pool = pool
    }

    fun recycle() {
        pool?.recycle(this)
    }

    class Empty<E : Enum<E>>(override val id: E) : Paket() {
        companion object {
            inline operator fun <reified E : Enum<E>> invoke(): Empty<E> {
                return Empty(enumValues<E>()[0])
            }
        }
    }

    companion object : BinaryFormat {

        override val context = EmptyModule

        override fun <T> dump(serializer: SerializationStrategy<T>, value: T): ByteArray {
            val output = ByteArrayOutput()
            val encoder = PaketEncoder(output)
            serializer.serialize(encoder, value)
            return output.toByteArray()
        }

        override fun <T> load(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
            val input = ByteArrayInput(bytes)
            val decoder = PaketDecoder(input)
            return deserializer.deserialize(decoder)
        }
    }
}

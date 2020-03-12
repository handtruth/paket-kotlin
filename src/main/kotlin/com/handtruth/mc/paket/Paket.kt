package com.handtruth.mc.paket

import kotlinx.io.*
import kotlin.reflect.full.companionObjectInstance

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

    fun recycle(): Boolean {
        val companion = this::class.companionObjectInstance
        if (companion !is PaketPool<*>)
            return false
        assert(companion.`class` == this::class) { "PaketPool must be the same type as paket" }
        @Suppress("UNCHECKED_CAST")
        companion as PaketPool<Paket>
        companion.recycle(this)
        return true
    }
}

package com.handtruth.mc.paket

abstract class Paket {
    abstract val id: Enum<*>

    internal val fields = mutableListOf<Field<*>>()

    val size get() = sizeVarInt(id.ordinal) + fields.sumBy { it.size }

    override fun equals(other: Any?): Boolean {
        if (other is Paket && id == other.id && fields.size == other.fields.size) {
            for (i in 0 until fields.size)
                if (fields[i] != other.fields[i])
                    return false
            return true
        }
        return false
    }
    override fun hashCode() = id.hashCode() + fields.hashCode()
    override fun toString(): String {
        val builder = StringBuilder(id.toString()).append(":{ ")
        for (i in 0 until fields.size - 1)
            builder.append(fields[i].toString()).append("; ")
        if (fields.isNotEmpty())
            builder.append(fields.last().toString()).append(' ')
        builder.append('}')
        return builder.toString()
    }
}

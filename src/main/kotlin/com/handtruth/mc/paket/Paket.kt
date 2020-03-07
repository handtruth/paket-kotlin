package com.handtruth.mc.paket

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
}

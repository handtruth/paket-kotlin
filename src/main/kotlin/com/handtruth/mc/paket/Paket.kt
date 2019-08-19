package com.handtruth.mc.paket

import kotlin.reflect.KProperty
import kotlin.reflect.full.staticFunctions

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

    abstract class Field<T: Any> internal constructor(paket: Paket) {
        init {
            @Suppress("LeakingThis")
            paket.fields += this
        }

        abstract val size: Int
        abstract fun read(stream: AsyncInputStream)
        abstract suspend fun readAsync(stream: AsyncInputStream)
        abstract fun write(stream: AsyncOutputStream)
        abstract suspend fun writeAsync(stream: AsyncOutputStream)

        lateinit var value: T
        operator fun getValue(me: Paket, property: KProperty<*>): T {
            return value
        }
        operator fun setValue(me: Paket, property: KProperty<*>, value: T) {
            this.value = value
        }

        override fun equals(other: Any?) = other is Field<*> && value == other.value
        override fun hashCode() = value.hashCode()
        override fun toString() = value.toString()
    }

    private class VarInt(paket: Paket) : Field<Int>(paket) {
        override val size get() = sizeVarInt(value)
        override fun read(stream: AsyncInputStream) {
            value = readVarInt(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = readVarIntAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            writeVarInt(stream, value)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeVarIntAsync(stream, value)
        }
    }
    protected fun varInt(): Field<Int> = VarInt(this)
    protected fun varInt(initial: Int) = varInt().apply { value = initial }

    private class EnumVarInt<E: Enum<E>>(paket: Paket, val values: Array<E>) : Field<E>(paket) {
        override val size get() = sizeVarInt(value.ordinal)
        override fun read(stream: AsyncInputStream) {
            value = values[readVarInt(stream)]
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = values[readVarIntAsync(stream)]
        }
        override fun write(stream: AsyncOutputStream) {
            writeVarInt(stream, value.ordinal)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeVarIntAsync(stream, value.ordinal)
        }
    }
    protected fun <E: Enum<E>> enumField(values: Array<E>): Field<E> = EnumVarInt(this, values)
    protected fun <E: Enum<E>> enumField(initial: E, values: Array<E>): Field<E> =
        enumField(values).apply { value = initial }
    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified E: Enum<E>> enumField(): Field<E> =
        enumField(E::class.staticFunctions.find { it.name == "values" }!!.call() as Array<E>)
    protected inline fun <reified E: Enum<E>> enumField(initial: E): Field<E> = enumField<E>().apply { value = initial }

    private class PaketString(paket: Paket) : Field<String>(paket) {
        override val size get() = sizeString(value)
        override fun read(stream: AsyncInputStream) {
            value = readString(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = readStringAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            writeString(stream, value)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeStringAsync(stream, value)
        }
        override fun toString() = "\"$value\""
    }
    protected fun string(): Field<String> = PaketString(this)
    protected fun string(initial: String) = string().apply { value = initial }

    private class PaketBool(paket: Paket) : Field<Boolean>(paket) {
        override val size = sizeBoolean
        override fun read(stream: AsyncInputStream) {
            value = readBoolean(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = readBooleanAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            writeBoolean(stream, value)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeBooleanAsync(stream, value)
        }
    }
    protected fun boolean(): Field<Boolean> = PaketBool(this)
    protected fun boolean(initial: Boolean) = boolean().apply { value = initial }

    private class PaketUInt8(paket: Paket): Field<Byte>(paket) {
        override val size = sizeByte
        override fun read(stream: AsyncInputStream) {
            value = readByte(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = readByteAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            writeByte(stream, value)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeByteAsync(stream, value)
        }
    }
    protected fun byte(): Field<Byte> = PaketUInt8(this)
    protected fun byte(initial: Byte) = byte().apply { value = initial }

    private class PaketUInt16(paket: Paket): Field<Int>(paket) {
        override val size = sizeShort
        override fun read(stream: AsyncInputStream) {
            value = readShort(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = readShortAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            writeShort(stream, value)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeShortAsync(stream, value)
        }
    }
    protected fun uint16(): Field<Int> = PaketUInt16(this)
    protected fun uint16(initial: Int) = uint16().apply { value = initial }

    private class PaketInt64(paket: Paket): Field<Long>(paket) {
        override val size = sizeLong
        override fun read(stream: AsyncInputStream) {
            value = readLong(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            value = readLongAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            writeLong(stream, value)
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            writeLongAsync(stream, value)
        }
    }
    protected fun int64(): Field<Long> = PaketInt64(this)
    protected fun int64(initial: Long) = int64().apply { value = initial }

    private abstract class PaketList<T> (paket: Paket, initial: MutableList<T>) : Field<MutableList<T>>(paket) {
        init {
            value = initial
        }

        override val size get() = sizeVarInt(value.size) + value.sumBy { sizeValue(it) }
        override fun read(stream: AsyncInputStream) {
            val size = readVarInt(stream)
            val it = value
            it.clear()
            for (i in 1..size)
                it += readValue(stream)
        }
        override suspend fun readAsync(stream: AsyncInputStream) {
            val size = readVarIntAsync(stream)
            val it = value
            it.clear()
            for (i in 1..size)
                it += readValueAsync(stream)
        }
        override fun write(stream: AsyncOutputStream) {
            val it = value
            val size = it.size
            writeVarInt(stream, size)
            it.forEach { writeValue(stream, it) }
        }
        override suspend fun writeAsync(stream: AsyncOutputStream) {
            val it = value
            val size = it.size
            writeVarIntAsync(stream, size)
            it.forEach { writeValueAsync(stream, it) }
        }
        protected abstract fun sizeValue(value: T): Int
        protected abstract fun readValue(stream: AsyncInputStream): T
        protected abstract suspend fun readValueAsync(stream: AsyncInputStream): T
        protected abstract fun writeValue(stream: AsyncOutputStream, value: T)
        protected abstract suspend fun writeValueAsync(stream: AsyncOutputStream, value: T)
    }

    private class VarIntList(paket: Paket, initial: MutableList<Int>) : PaketList<Int>(paket, initial) {
        override fun sizeValue(value: Int) = sizeVarInt(value)
        override fun readValue(stream: AsyncInputStream) = readVarInt(stream)
        override suspend fun readValueAsync(stream: AsyncInputStream) = readVarIntAsync(stream)
        override fun writeValue(stream: AsyncOutputStream, value: Int) = writeVarInt(stream, value)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Int) = writeVarIntAsync(stream, value)
    }
    protected fun listOfVarInt(initial: MutableList<Int> = mutableListOf()): Field<MutableList<Int>> =
            VarIntList(this, initial)

    private class EnumList<E: Enum<E>>(paket: Paket, initial: MutableList<E>,
                                       val values: Array<E>) : PaketList<E>(paket, initial) {
        override fun sizeValue(value: E) = sizeVarInt(value.ordinal)
        override fun readValue(stream: AsyncInputStream) = values[readVarInt(stream)]
        override suspend fun readValueAsync(stream: AsyncInputStream) = values[readVarIntAsync(stream)]
        override fun writeValue(stream: AsyncOutputStream, value: E) = writeVarInt(stream, value.ordinal)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: E) = writeVarIntAsync(stream, value.ordinal)
    }
    protected fun <E: Enum<E>> listOfEnum(initial: MutableList<E> = mutableListOf(), values: Array<E>): Field<MutableList<E>> =
        EnumList(this, initial, values)
    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified E: Enum<E>> listOfEnum(initial: MutableList<E> = mutableListOf()): Field<MutableList<E>> =
        listOfEnum(initial, E::class.staticFunctions.find { it.name == "values" }!!.call() as Array<E>)

    private class PaketStringList(paket: Paket, initial: MutableList<String>) : PaketList<String>(paket, initial) {
        override fun sizeValue(value: String) = sizeString(value)
        override fun readValue(stream: AsyncInputStream) = readString(stream)
        override suspend fun readValueAsync(stream: AsyncInputStream) = readStringAsync(stream)
        override fun writeValue(stream: AsyncOutputStream, value: String) = writeString(stream, value)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: String) = writeStringAsync(stream, value)
    }
    protected fun listOfString(initial: MutableList<String> = mutableListOf()): Field<MutableList<String>> =
            PaketStringList(this, initial)

    private class PaketBoolList(paket: Paket, initial: MutableList<Boolean>) : PaketList<Boolean>(paket, initial) {
        override fun sizeValue(value: Boolean) = sizeBoolean
        override fun readValue(stream: AsyncInputStream) = readBoolean(stream)
        override suspend fun readValueAsync(stream: AsyncInputStream) = readBooleanAsync(stream)
        override fun writeValue(stream: AsyncOutputStream, value: Boolean) = writeBoolean(stream, value)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Boolean) = writeBooleanAsync(stream, value)
    }
    protected fun listOfBoolean(initial: MutableList<Boolean> = mutableListOf()): Field<MutableList<Boolean>> =
            PaketBoolList(this, initial)

    private class PaketUInt8List(paket: Paket, initial: MutableList<Byte>) : PaketList<Byte>(paket, initial) {
        override fun sizeValue(value: Byte) = sizeByte
        override fun readValue(stream: AsyncInputStream) = readByte(stream)
        override suspend fun readValueAsync(stream: AsyncInputStream) = readByteAsync(stream)
        override fun writeValue(stream: AsyncOutputStream, value: Byte) = writeByte(stream, value)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Byte) = writeByteAsync(stream, value)
    }
    protected fun listOfByte(initial: MutableList<Byte> = mutableListOf()): Field<MutableList<Byte>> =
            PaketUInt8List(this, initial)

    private class PaketUInt16List(paket: Paket, initial: MutableList<Int>) : PaketList<Int>(paket, initial) {
        override fun sizeValue(value: Int) = sizeShort
        override fun readValue(stream: AsyncInputStream) = readShort(stream)
        override suspend fun readValueAsync(stream: AsyncInputStream) = readShortAsync(stream)
        override fun writeValue(stream: AsyncOutputStream, value: Int) = writeShort(stream, value)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Int) = writeShortAsync(stream, value)
    }
    protected fun listOfUInt16(initial: MutableList<Int> = mutableListOf()): Field<MutableList<Int>> =
        PaketUInt16List(this, initial)

    private class PaketInt64List(paket: Paket, initial: MutableList<Long>) : PaketList<Long>(paket, initial) {
        override fun sizeValue(value: Long) = sizeLong
        override fun readValue(stream: AsyncInputStream) = readLong(stream)
        override suspend fun readValueAsync(stream: AsyncInputStream) = readLongAsync(stream)
        override fun writeValue(stream: AsyncOutputStream, value: Long) = writeLong(stream, value)
        override suspend fun writeValueAsync(stream: AsyncOutputStream, value: Long) = writeLongAsync(stream, value)
    }
    protected fun listOfInt64(initial: MutableList<Long> = mutableListOf()): Field<MutableList<Long>> =
        PaketInt64List(this, initial)
}

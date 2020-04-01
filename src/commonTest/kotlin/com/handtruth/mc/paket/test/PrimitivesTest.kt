package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.path
import com.handtruth.mc.paket.fields.string
import com.handtruth.mc.paket.util.Path
import io.ktor.test.dispatcher.testSuspend
import io.ktor.utils.io.core.toByteArray
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class PrimitivesTest {

    @Test
    fun sizeStringTest() {
        val stringA = "Lol Kek Kotlin berg"
        val sizeA = sizeString(stringA) - 1
        assertEquals(stringA.toByteArray().size, sizeA)
        val stringB = "Русская строка со словом ЖОПА"
        val sizeB = sizeString(stringB) - 1
        assertEquals(stringB.toByteArray().size, sizeB)

        val outputA = ByteArrayOutput()
        writeString(outputA, stringA)
        assertEquals(sizeA, outputA.toByteArray().size - 1)
        val outputB = ByteArrayOutput()
        writeString(outputB, stringB)
        assertEquals(sizeB, outputB.toByteArray().size - 1)
    }

    @Test
    fun writeReadLong() {
        val output = ByteArrayOutput()
        writeLong(output, -1)
        val bytes = output.toByteArray()
        assertEquals(listOf<Byte>(-1, -1, -1, -1, -1, -1, -1, -1), bytes.asList())
        val input = ByteArrayInput(bytes)
        assertEquals(-1L, readLong(input))
    }


    @Test
    fun readWriteLong() {
        val bytes = ByteArray(8) {
            if (it < 4) -1 else 0
        }
        val input = ByteArrayInput(bytes)
        val output = ByteArrayOutput()
        val n = readLong(input)
        println("$n != -1")
        writeLong(output, n)
        assertEquals(bytes.asList(), output.toByteArray().asList())
    }

    @Suppress("unused")
    enum class SomeIDs {
        Zero, One, Two, Three
    }

    class SimplePaket : Paket() {
        override val id: Enum<*> = SomeIDs.Two

        companion object : PaketCreator<SimplePaket> {
            override fun produce() = SimplePaket()
        }
    }

    class WithStringPaket(s: String = "") : Paket() {
        override val id = SomeIDs.Three

        init { string(s) }

        companion object : PaketCreator<WithStringPaket> {
            override fun produce() = WithStringPaket()
        }
    }

    @Test
    fun sizeVarIntCheck() {
        assertEquals(1, sizeVarInt(0))
        assertEquals(1, sizeVarInt(35))
        assertEquals(5, sizeVarInt(-1))
        assertEquals(3, sizeVarInt(56845))
        assertEquals(1, SimplePaket().size)
        assertEquals(12, WithStringPaket("lolkapopka").size)
    }

    class PathPaket(location: String = "") : Paket() {
        override val id = SomeIDs.Zero
        var location by path(location)

        override fun clear() {
            location = Path.empty
        }

        companion object : AbstractPaketPool<PathPaket>() {
            override fun create() = PathPaket()
        }
    }

    @Test
    fun checkPath() = testSuspend {
        val paket = writeReadPaket(PathPaket("/usr/local/share/doc"), PathPaket)
        paket.recycle()
        assertEquals(Path.empty, paket.location)
        writeReadPaket(PathPaket(".local/storage"), PathPaket).recycle()
        writeReadPaket(PathPaket(""), PathPaket).recycle()
        writeReadPaket(PathPaket("/"), PathPaket).recycle()
        writeReadPaket(PathPaket("ktlo"), PathPaket).recycle()
        paket.recycle()
    }
}

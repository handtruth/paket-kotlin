package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.PathField
import com.handtruth.mc.paket.fields.path
import com.handtruth.mc.paket.fields.string
import com.handtruth.mc.paket.util.Path
import kotlinx.coroutines.runBlocking
import kotlinx.io.ByteArrayInput
import kotlinx.io.ByteArrayOutput
import kotlinx.io.pool.DefaultPool
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrimitivesTest {

    @Test
    fun `Test sizeString`() {
        val stringA = "Lol Kek Cheburek"
        val sizeA = sizeString(stringA) - 1
        assertEquals(stringA.toByteArray().size, sizeA)
        val stringB = "Русская строка со словом ЖОПА"
        val sizeB = sizeString(stringB) - 1
        assertEquals(stringB.toByteArray().size, sizeB)
    }

    @Test
    fun `Write Read Long`() {
        val output = ByteArrayOutput()
        writeLong(output, -1)
        val bytes = output.toByteArray()
        assertEquals(listOf<Byte>(-1, -1, -1, -1, -1, -1, -1, -1), bytes.asList())
        val input = ByteArrayInput(bytes)
        assertEquals(-1L, readLong(input))
    }


    @Test
    fun `Read Write Long`() {
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
    }

    class WithStringPaket(s: String = "") : Paket() {
        override val id = SomeIDs.Three

        init { string(s) }
    }

    @Test
    fun `Check sizeVarInt`() {
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

        companion object : PaketPool<PathPaket>(PathPaket::class)
    }

    @Test
    fun `Check path`() {
        runBlocking {
            val paket = writeReadPaket(PathPaket("/usr/local/share/doc"))
            assertTrue(paket.recycle())
            assertEquals(Path.empty, paket.location)
            writeReadPaket(PathPaket(".local/storage")).recycle()
            writeReadPaket(PathPaket("")).recycle()
            writeReadPaket(PathPaket("/")).recycle()
            writeReadPaket(PathPaket("ktlo")).recycle()
        }
    }
    object PathPaketPool : DefaultPool<PathPaket>(10) {
        override fun produceInstance() = PathPaket()
    }

    @Test
    fun `lol kek`() {
        val paket = PathPaketPool.borrow()
        PathPaketPool.recycle(paket)
    }
}

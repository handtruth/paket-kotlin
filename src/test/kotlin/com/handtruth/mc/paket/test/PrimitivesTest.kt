package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.readLong
import com.handtruth.mc.paket.sizeVarInt
import com.handtruth.mc.paket.writeLong
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

class PrimitivesTest {
    @Test
    fun `Write Read Long`() {
        val output = ByteArrayOutputStream()
        val stream1 = FakeAsyncStream(output)
        writeLong(stream1, -1)
        val bytes = output.toByteArray()
        assertEquals(listOf<Byte>(-1, -1, -1, -1, -1, -1, -1, -1), bytes.asList())
        val input = ByteArrayInputStream(bytes)
        val stream2 = FakeAsyncStream(input)
        assertEquals(-1L, readLong(stream2))
    }


    @Test
    fun `Read Write Long`() {
        val bytes = ByteArray(8) {
            if (it < 4) -1 else 0
        }
        val input = ByteArrayInputStream(bytes)
        val output = ByteArrayOutputStream()
        val stream = FakeAsyncStream(input, output)
        val n = readLong(stream)
        println("$n != -1")
        writeLong(stream, n)
        assertEquals(bytes.asList(), output.toByteArray().asList())
    }

    enum class SomeIDs {
        Zero, One, Two, Three
    }

    class SimplePaket : Paket() {
        override val id: Enum<*> = SomeIDs.Two
    }

    class WithStringPaket(s: String = "") : Paket() {
        override val id = SomeIDs.Three

        var s by string(s)
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
}

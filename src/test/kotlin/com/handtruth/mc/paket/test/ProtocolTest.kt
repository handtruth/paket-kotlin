package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.IllegalProtocolStateException
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketTransmitter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class ProtocolTest {

    @Suppress("unused")
    enum class ExampleID { First, Second }
    enum class ExampleEnum {
        Zero, One, Two, Three
    }

    @Suppress("unused")
    class ExamplePaket(pVarInt: Int = 0,
                       pEnum: ExampleEnum = ExampleEnum.Zero,
                       pString: String = "",
                       pBool: Boolean = false,
                       pUInt8: Byte = 0,
                       pUInt16: Int = 0,
                       pInt64: Long = 0,
                       pListVarInt: List<Int> = emptyList(),
                       pListEnum: List<ExampleEnum> = emptyList(),
                       pListString: List<String> = emptyList(),
                       pListBool: List<Boolean> = emptyList(),
                       pListUInt8: List<Byte> = emptyList(),
                       pListUInt16: List<Int> = emptyList(),
                       pListInt64: List<Long> = emptyList()
                       ) : Paket() {
        override val id = ExampleID.Second
        var pVarInt by varInt(pVarInt)
        var pEnum by enumField(pEnum)
        var pString by string(pString)
        var pBool by boolean(pBool)
        var pUInt8 by byte(pUInt8)
        var pUInt16 by uint16(pUInt16)
        var pInt64 by int64(pInt64)
        var pListVarInt by listOfVarInt(pListVarInt.toMutableList())
        var pListEnum by listOfEnum(pListEnum.toMutableList())
        var pListString by listOfString(pListString.toMutableList())
        var pListBool by listOfBoolean(pListBool.toMutableList())
        var pListUInt8 by listOfByte(pListUInt8.toMutableList())
        var pListUInt16 by listOfUInt16(pListUInt16.toMutableList())
        var pListInt64 by listOfInt64(pListInt64.toMutableList())
    }

    private val paket get() = ExamplePaket(
        56845, ExampleEnum.Two, "lolkekdude", true, 68, 65535,
        System.currentTimeMillis(), listOf(64, -668, 894615, 6, -65321, -1),
        listOf(ExampleEnum.Three, ExampleEnum.One, ExampleEnum.Zero, ExampleEnum.Two),
        listOf("lol", "kek", "dude", "popka".repeat(450)), listOf(true, false, true), listOf(89, -128, 127, 8),
        listOf(Short.MAX_VALUE + 1, 48453, 0), listOf(895, System.currentTimeMillis(), -852)
    )

    @Test
    fun `String Paket`() {
        class StringPaket(string: String = "") : Paket() {
            override val id = ExampleID.First
            var string by string(string)
        }
        val paketA = StringPaket("English String with BLYAT")
        val output = ByteArrayOutputStream()
        val ts1 = PaketTransmitter.create(object: InputStream() {
            override fun read() = throw NotImplementedError()
        }, output)
        ts1.write(paketA)
        val bytes = output.toByteArray()
        val input = ByteArrayInputStream(bytes)
        val ts2 = PaketTransmitter.create(input, output)
        val paketB: StringPaket = ts2.read()
        assertEquals(paketA, paketB)
        assertEquals(paketA.toString(), paketB.toString())
        assertEquals(paketA.hashCode(), paketB.hashCode())
    }

    @Test
    fun `Write and read`() {
        val paketA = paket
        val output = ByteArrayOutputStream()
        val ts1 = PaketTransmitter.create(object: InputStream() {
            override fun read() = throw NotImplementedError()
        }, output)
        ts1.write(paketA)
        val bytes = output.toByteArray()
        val input = ByteArrayInputStream(bytes)
        val ts2 = PaketTransmitter.create(input, output)
        val paketB: ExamplePaket = ts2.read()
        assertEquals(paketA, paketB)
        assertEquals(paketA.toString(), paketB.toString())
        assertEquals(paketA.hashCode(), paketB.hashCode())
        paketB.pBool = false
        assertNotEquals(paketA, paketB)
        assertNotEquals(paketA.hashCode(), paketB.hashCode())

        assertFailsWith<UnsupportedOperationException> {
            runBlocking {
                ts1.writeAsync(paketA)
            }
        }
        assertFailsWith<UnsupportedOperationException> {
            runBlocking {
                ts2.catchOrdinalAsync()
            }
        }

        assertNotEquals(Any(), paketB)

        ts1.close()
        ts2.close()
    }

    @Test
    fun `Write and read async`() = runBlocking {
        val paketA = paket
        val output = ByteArrayOutputStream()
        val fakeOut = FakeAsyncStream(output)
        val ts1 = PaketTransmitter(fakeOut, fakeOut)
        ts1.writeAsync(paketA)
        val bytes = output.toByteArray()
        val input = ByteArrayInputStream(bytes)
        val fakeIn = FakeAsyncStream(input)
        val ts2 = PaketTransmitter(fakeIn, fakeIn)
        val paketB: ExamplePaket = ts2.readAsync()
        assertEquals(paketA, paketB)
        ts1.close()
        ts2.close()
    }

    class SimplePaket : Paket() {
        override val id = ExampleID.First
    }

    @Test
    fun `Simple Paket`() {
        val paketA = SimplePaket()
        val output = ByteArrayOutputStream()
        val ts1 = PaketTransmitter.create(object: InputStream() {
            override fun read() = throw NotImplementedError()
        }, output)
        ts1.write(paketA)
        val bytes = output.toByteArray()
        assertEquals(2, bytes.size)
        val input = ByteArrayInputStream(bytes)
        val ts2 = PaketTransmitter.create(input, output)
        val id = ts2.catchOrdinal()
        assertEquals(1, ts2.size)
        assertEquals(ExampleID.First.ordinal, id)
        val paketB: SimplePaket = ts2.read()
        assertEquals(paketA, paketB)
    }

    @Test
    fun `Drop pakets`() {
        val paketA = paket
        val output = ByteArrayOutputStream()
        val ts1 = PaketTransmitter.create(object: InputStream() {
            override fun read() = throw NotImplementedError()
        }, output)
        ts1.write(ExamplePaket(23))
        ts1.write(SimplePaket())
        ts1.write(paketA)
        val bytes = output.toByteArray()
        val input = ByteArrayInputStream(bytes)
        val ts2 = PaketTransmitter.create(input, output)
        ts2.drop()
        assertEquals(ExampleID.First.ordinal, ts2.catchOrdinal())
        assertEquals(ExampleID.Second.ordinal, ts2.catchOrdinal())
        assertFailsWith<IllegalProtocolStateException> {
            ts2.read<SimplePaket>()
        }
    }

    @Test
    fun `Drop pakets async`() = runBlocking {
        val paketA = paket
        val output = ByteArrayOutputStream()
        val fake1 = FakeAsyncStream(output)
        val ts1 = PaketTransmitter(fake1, fake1)
        ts1.writeAsync(ExamplePaket(23))
        ts1.writeAsync(SimplePaket())
        ts1.writeAsync(paketA)
        val bytes = output.toByteArray()
        val input = ByteArrayInputStream(bytes)
        val ts2 = PaketTransmitter(FakeAsyncStream(input), fake1)
        ts2.dropAsync()
        assertEquals(ExampleID.First.ordinal, ts2.catchOrdinalAsync())
        assertEquals(ExampleID.Second.ordinal, ts2.catchOrdinalAsync())
        assertFailsWith<IllegalProtocolStateException> {
            ts2.readAsync<SimplePaket>()
        }
        return@runBlocking
    }
}

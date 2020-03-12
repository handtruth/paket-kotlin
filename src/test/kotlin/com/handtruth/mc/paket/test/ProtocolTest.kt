package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.fields.*
import kotlinx.coroutines.runBlocking
import kotlinx.io.*
import org.junit.jupiter.api.Test
import kotlin.io.use
import kotlin.test.assertEquals
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
                       pVarLong: Long = 0,
                       pPath: String = "",
                       pListVarInt: List<Int> = emptyList(),
                       pListEnum: List<ExampleEnum> = emptyList(),
                       pListString: List<String> = emptyList(),
                       pListBool: List<Boolean> = emptyList(),
                       pListUInt8: List<Byte> = emptyList(),
                       pListUInt16: List<UShort> = emptyList(),
                       pListInt64: List<Long> = emptyList(),
                       pListVarLong: List<Long> = emptyList(),
                       pListPath: List<String> = emptyList()
                       ) : Paket() {
        override val id = ExampleID.Second
        var pVarInt by varInt(pVarInt)
        var pEnum by enum(pEnum)
        //var pString by string(pString)
        var pBool by bool(pBool)
        var pUInt8 by byte(pUInt8)
        var pUInt16 by uint16(pUInt16.toUShort())
        var pInt64 by int64(pInt64)
        var pPath by path(pPath)
        var pVarLong by varLong(pVarLong)
        //var pListVarInt by listOfVarInt(pListVarInt)
        //var pListEnum by listOfEnum(pListEnum)
        //var pListString by listOfString(pListString)
        //var pListBool by listOfBool(pListBool)
        //var pListUInt8 by listOfByte(pListUInt8)
        //var pListUInt16 by listOfUint16(pListUInt16)
        //var pListInt64 by listOfInt64(pListInt64)
        //var pListVarLong by listOfVarLong(pListVarLong)
        //var pListPath by listOfPath(pListPath.map { Path(it) })
    }

    private val paket get() = ExamplePaket(
        56845, ExampleEnum.Two, "lolkekdude", true, 68, 65535,
        System.currentTimeMillis(), System.currentTimeMillis(), "/usr/local/share/doc",
        listOf(64, -668, 894615, 6, -65321, -1),
        listOf(ExampleEnum.Three, ExampleEnum.One, ExampleEnum.Zero, ExampleEnum.Two),
        listOf("lol", "kek", "dude", "popka".repeat(450), "/usr/local/share/doc", ".local/storage", "", "/"),
        listOf(true, false, true), listOf(89, -128, 127, 8),
        listOf((Short.MAX_VALUE + 1).toUShort(), 48453u, 0u), listOf(895, System.currentTimeMillis(), -852),
        listOf(0, Short.MAX_VALUE.toLong() + 1L, 8373, 0x223L, System.currentTimeMillis(), -543),
        listOf("/usr/local/share/doc", ".local/storage", "", "/", "ktlo")
    )

    @Test
    fun `String Paket`() {
        runBlocking {
            class StringPaket(string: String = "") : Paket() {
                override val id = ExampleID.First
                var string by string(string)
            }
            val paketA = StringPaket("English String with BLYAT")
            writeReadPaket(paketA)
            val expected = "Русская строка с ЖОПОЙ"
            paketA.string = expected
            assertEquals(expected, paketA.fields.first().value)
        }
    }

    @Test
    fun `Write and read`() = runBlocking {
        val paketA = paket
        val paketB = writeReadPaket(paketA)
        paketB.pBool = false
        assertNotEquals(paketA, paketB)
        assertNotEquals(paketA.hashCode(), paketB.hashCode())
        assertNotEquals(Any(), paketB)
    }

    class SimplePaket : Paket() {
        override val id = ExampleID.First
    }

    @Test
    fun `Simple Paket`() {
        runBlocking {
            val paketB = writeReadPaket(SimplePaket())
            assertEquals(ExampleID.First, paketB.id)
        }
    }

    @Test
    fun `Drop pakets`() {
        runBlocking {
            val paketA = paket
            val output = ByteArrayOutput()
            val sender = PaketSender(output)
            sender.use {
                sender.send(ExamplePaket(23))
                sender.send(SimplePaket())
                sender.send(paketA)
            }
            val bytes = output.toByteArray()
            val input = ByteArrayInput(bytes)
            val receiver = PaketReceiver(input)
            receiver.drop()
            assertEquals(ExampleID.First.ordinal, receiver.catchOrdinal())
            assertEquals(ExampleID.Second.ordinal, receiver.catchOrdinal())
            assertFailsWith<IllegalProtocolStateException> {
                receiver.receive<SimplePaket>()
            }
        }
    }

    //@Test
    fun `bug test`() {
        val bytesExpected = ByteArray(15) { it.toByte() }
        val output = ByteArrayOutput()
        output.writeByteArray(bytesExpected)
        val input = ByteArrayInput(output.toByteArray())
        assertEquals(bytesExpected.toList(), input.readByteArray(bytesExpected.size).toList())
    }

    @Test
    fun `bugless test`() {
        val bytesExpected = ByteArray(15) { it.toByte() }
        val output = ByteArrayOutput()
        bytesExpected.forEach { output.writeByte(it) }
        output.flush()
        val input = ByteArrayInput(output.toByteArray())
        val actualBytes = ByteArray(bytesExpected.size)
        actualBytes.forEachIndexed { i, _ -> actualBytes[i] = input.readByte() }
        assertEquals(bytesExpected.toList(), actualBytes.toList())
    }
}

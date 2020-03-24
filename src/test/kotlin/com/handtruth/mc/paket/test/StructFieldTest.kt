package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketSender
import com.handtruth.mc.paket.fields.Int64Encoder
import com.handtruth.mc.paket.fields.EncodeWith
import com.handtruth.mc.paket.fields.struct
import kotlinx.coroutines.runBlocking
import kotlinx.io.ByteArrayOutput
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StructFieldTest {

    data class CustomObject(
        val lol: Int = 0,
        val kek: Long = 0L,
        @EncodeWith(Int64Encoder::class)
        val timestamp: Long = 0L
    )

    enum class Numbers {
        One, Two, Three
    }

    class CustomObjectPaket(obj: CustomObject = CustomObject()) : Paket() {
        override val id = Numbers.One

        var obj by struct(obj)
    }

    @Test
    fun `custom object`() {
        runBlocking {
            val paket = writeReadPaket(CustomObjectPaket(CustomObject(39, 4268559, System.currentTimeMillis())))
            paket.obj = CustomObject(0,-1, -1)
            val output = ByteArrayOutput()
            PaketSender(output).use {
                it.send(paket)
            }
            val bytes = output.toByteArray()
            assertEquals(3 + 10 + 8, bytes.size)
        }
    }

}

package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.JvmPaketCreator
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketSender
import com.handtruth.mc.paket.WithCodec
import com.handtruth.mc.paket.fields.Int64Codec
import com.handtruth.mc.paket.fields.struct
import kotlinx.coroutines.runBlocking
import kotlinx.io.ByteArrayOutput
import org.junit.Test
import kotlin.test.assertEquals

class StructFieldTest {

    data class CustomObject(
        val lol: Int = 0,
        val kek: Long = 0L,
        @WithCodec(Int64Codec::class)
        val timestamp: Long = 0L
    )

    enum class Numbers {
        One, Two, Three
    }

    class CustomObjectPaket(obj: CustomObject = CustomObject()) : Paket() {
        override val id = Numbers.One

        var obj by struct(obj)

        companion object : JvmPaketCreator<CustomObjectPaket>(CustomObjectPaket::class)
    }

    @Test
    fun `custom object`() {
        runBlocking {
            val paket = writeReadPaket(
                CustomObjectPaket(CustomObject(39, 4268559, System.currentTimeMillis())), CustomObjectPaket)
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

package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketCreator
import com.handtruth.mc.paket.fields.json
import com.handtruth.mc.paket.fields.nbt
import com.handtruth.mc.paket.fields.serial
import io.ktor.test.dispatcher.testSuspend
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.test.Test

class NBTFormatTest {

    @Serializable
    data class Attribute(
        val id: Int = 0,
        val durability: Short = 0,
        val isPrimal: Boolean = false,
        val amount: Byte = 0,
        val delay: Long = 0,
        val speed: Float = 0f,
        val movement: Double = .0
    )

    @Serializable
    data class Player(
        val name: String = "",
        val health: Long = 0,
        val attributes: Map<Int, Attribute> = emptyMap()
    )

    @Serializable
    data class DataForNBT(
        val string: String = "",
        val integer: Int = 0,
        val list: List<Float> = emptyList(),
        val player: Player = Player()
    )

    enum class NBTDummyID {
        Ohh
    }

    class NBTTestPaket(dataA: DataForNBT = DataForNBT(), dataB: DataForNBT = DataForNBT()) : Paket() {
        override val id = NBTDummyID.Ohh

        val dataA by nbt(dataA)
        val dataB by serial(dataB)
        val dataC by json(dataB)

        companion object : PaketCreator<NBTTestPaket> {
            override fun produce() = NBTTestPaket()
        }
    }

    @Test
    fun readWriteNBT() = testSuspend {
        writeReadPaket(NBTTestPaket(
            DataForNBT("Lol kek Kotlin berg", 5468, listOf(3.0f, 5.6f, -89f)),
            DataForNBT("Русская строка с необычными символами для ASCII",
                -89765, listOf(0.2f, -34.5f),
                Player("Ktlo", 24982984545979, mapOf(
                        668 to Attribute(
                            10, 569, true, 68,
                            Random.nextLong(), 5688f, 647878.343
                        )
                    )
                )
            )
        ), NBTTestPaket)
    }
}

package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.JvmPaketCreator
import com.handtruth.mc.paket.JvmPaketPool
import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.fields.bool
import com.handtruth.mc.paket.fields.int8
import com.handtruth.mc.paket.fields.string
import com.handtruth.mc.paket.fields.varLong
import com.handtruth.mc.paket.take
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class PerformanceTest {
    enum class IDS {
        Create, Rent
    }

    class CreatePaket(pString: String = "lolkek",
                      pVarLong: Long = Random.nextLong(),
                      pInt8: Byte = 230.toByte(), pBool: Boolean = true) : Paket() {
        override val id = IDS.Create
        var pString by string(pString)
        var pVarLong by varLong(pVarLong)
        var pInt8 by int8(pInt8)
        var pBool by bool(pBool)
        var pString2 by string(pString)
        var pVarLong2 by varLong(pVarLong)
        var pInt82 by int8(pInt8)
        var pBool2 by bool(pBool)
        var pString3 by string(pString)
        var pVarLong3 by varLong(pVarLong)
        var pInt83 by int8(pInt8)
        var pBool3 by bool(pBool)

        companion object : JvmPaketCreator<CreatePaket>(CreatePaket::class)
    }

    class RentPaket : Paket() {
        override val id = IDS.Rent
        var pString by string()
        var pVarLong by varLong()
        var pint8 by int8()
        var pBool by bool()
        var pString2 by string()
        var pVarLong2 by varLong()
        var pint82 by int8()
        var pBool2 by bool()
        var pString3 by string()
        var pVarLong3 by varLong()
        var pint83 by int8()
        var pBool3 by bool()

        operator fun invoke(pString: String = "lolkek", pVarLong: Long = Random.nextLong(),
                            pint8: Byte = 230.toByte(), pBool: Boolean = true): RentPaket {
            this.pString = pString
            this.pVarLong = pVarLong
            this.pint8 = pint8
            this.pBool = pBool
            this.pString2 = pString
            this.pVarLong2 = pVarLong
            this.pint82 = pint8
            this.pBool2 = pBool
            this.pString3 = pString
            this.pVarLong3 = pVarLong
            this.pint83 = pint8
            this.pBool3 = pBool
            return this
        }

        companion object : JvmPaketPool<RentPaket>(RentPaket::class)
    }

    //@Test
    fun `Paket Creation vs Paket Rent`() {
        runBlocking {
            val iterations = 100000
            RentPaket.take {
                writeReadPaket(it(), RentPaket).recycle()
            }
            writeReadPaket(CreatePaket(), CreatePaket)
            val rentTime = measureTimeMillis {
                for (i in 1..iterations) {
                    RentPaket.take {
                        writeReadPaket(it(), RentPaket).recycle()
                    }
                }
            }
            val createTime = measureTimeMillis {
                for (i in 1..iterations) {
                    writeReadPaket(CreatePaket(), CreatePaket)
                }
            }
            println("Create: $createTime ms, Rent: $rentTime ms")
        }
    }
}

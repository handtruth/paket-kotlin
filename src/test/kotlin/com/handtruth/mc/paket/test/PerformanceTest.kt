package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.Paket
import com.handtruth.mc.paket.PaketPool
import com.handtruth.mc.paket.fields.bool
import com.handtruth.mc.paket.fields.byte
import com.handtruth.mc.paket.fields.string
import com.handtruth.mc.paket.fields.varLong
import kotlinx.coroutines.runBlocking
import kotlinx.io.pool.useInstance
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class PerformanceTest {
    enum class IDS {
        Create, Rent
    }

    class CreatePaket(pString: String = "lolkek",
                      pVarLong: Long = System.currentTimeMillis(),
                      pByte: Byte = 230.toByte(), pBool: Boolean = true) : Paket() {
        override val id = IDS.Create
        var pString by string(pString)
        var pVarLong by varLong(pVarLong)
        var pByte by byte(pByte)
        var pBool by bool(pBool)
        var pString2 by string(pString)
        var pVarLong2 by varLong(pVarLong)
        var pByte2 by byte(pByte)
        var pBool2 by bool(pBool)
        var pString3 by string(pString)
        var pVarLong3 by varLong(pVarLong)
        var pByte3 by byte(pByte)
        var pBool3 by bool(pBool)
    }

    class RentPaket : Paket() {
        override val id = IDS.Rent
        var pString by string()
        var pVarLong by varLong()
        var pByte by byte()
        var pBool by bool()
        var pString2 by string()
        var pVarLong2 by varLong()
        var pByte2 by byte()
        var pBool2 by bool()
        var pString3 by string()
        var pVarLong3 by varLong()
        var pByte3 by byte()
        var pBool3 by bool()

        operator fun invoke(pString: String = "lolkek", pVarLong: Long = System.currentTimeMillis(),
                            pByte: Byte = 230.toByte(), pBool: Boolean = true): RentPaket {
            this.pString = pString
            this.pVarLong = pVarLong
            this.pByte = pByte
            this.pBool = pBool
            this.pString2 = pString
            this.pVarLong2 = pVarLong
            this.pByte2 = pByte
            this.pBool2 = pBool
            this.pString3 = pString
            this.pVarLong3 = pVarLong
            this.pByte3 = pByte
            this.pBool3 = pBool
            return this
        }

        companion object : PaketPool<RentPaket>(RentPaket::class)
    }

    //@Test
    fun `Paket Creation vs Paket Rent`() {
        runBlocking {
            val iterations = 100000
            RentPaket.useInstance {
                writeReadPaket(it()).recycle()
            }
            writeReadPaket(CreatePaket())
            val rentTime = measureTimeMillis {
                for (i in 1..iterations) {
                    RentPaket.useInstance {
                        writeReadPaket(it()).recycle()
                    }
                }
            }
            val createTime = measureTimeMillis {
                for (i in 1..iterations) {
                    writeReadPaket(CreatePaket())
                }
            }
            println("Create: $createTime ms, Rent: $rentTime ms")
        }
    }
}

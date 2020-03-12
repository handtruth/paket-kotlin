package com.handtruth.mc.paket

import kotlinx.io.buildBytes

fun Paket.toBytes() = buildBytes {
    writeVarInt(this, id.ordinal)
    for (field in fields)
        field.write(this)
}

package com.handtruth.mc.paket

import kotlinx.io.buildBytes

fun Paket.toBytes() = buildBytes {
    write(this)
}

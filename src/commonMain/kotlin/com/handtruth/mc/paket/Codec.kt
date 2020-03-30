package com.handtruth.mc.paket

import kotlinx.io.Input
import kotlinx.io.Output

interface Codec<T> {
    fun measure(value: T): Int
    fun read(input: Input, old: T?): T
    fun write(output: Output, value: T)
}

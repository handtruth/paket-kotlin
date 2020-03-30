@file:Suppress("FunctionName")

package com.handtruth.mc.paket

import kotlinx.coroutines.Dispatchers
import kotlinx.io.Input
import kotlinx.io.Output

fun PaketSender(output: Output) = PaketSender(output, Dispatchers.IO)
fun PaketReceiver(input: Input) = PaketReceiver(input, Dispatchers.IO)

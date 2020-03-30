package com.handtruth.mc.paket

import kotlinx.io.Closeable

class BrokenObjectException(message: String = "Forbidden operation on already broken object") :
    RuntimeException(message)

interface Breakable : Closeable {
    val broken: Boolean
}

abstract class AbstractBreakable : Breakable {
    override var broken = false
        protected set

    protected inline fun <R> breakableAction(lambda: () -> R): R {
        if (broken)
            throw BrokenObjectException("Connection already broken, consider reconnect")
        try {
            return lambda()
        } catch (thr: Throwable) {
            broken = true
            throw thr
        }
    }

    override fun close() {
        broken = true
    }
}

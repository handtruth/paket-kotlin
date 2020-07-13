package com.handtruth.mc.paket

import kotlinx.io.Closeable

class BrokenObjectException : RuntimeException {
    constructor() : super("broken object")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super("broken object", cause)
}

interface Breakable : Closeable {
    val broken: Boolean
}

abstract class AbstractBreakable : Breakable {
    override var broken = false
        protected set

    protected inline fun <R> breakableAction(lambda: () -> R): R {
        if (broken)
            throw BrokenObjectException("object broken, consider recreate")
        try {
            return lambda()
        } catch (e: Exception) {
            broken = true
            throw e
        }
    }

    override fun close() {
        broken = true
    }
}

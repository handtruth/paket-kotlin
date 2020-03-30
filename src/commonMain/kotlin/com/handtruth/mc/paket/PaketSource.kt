package com.handtruth.mc.paket

import kotlinx.io.pool.DefaultPool
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface PaketSource<out P: Paket> {
    fun produce(): P
}

interface PaketCreator<P: Paket> : PaketSource<P>

interface PaketPool<P: Paket> : PaketSource<P> {
    fun recycle(paket: P)
}

inline fun <P: Paket, R> PaketPool<P>.take(block: (P) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val paket = produce()
    try {
        return block(paket)
    } finally {
        recycle(paket)
    }
}

abstract class AbstractPaketPool<P: Paket>(capacity: Int = 25) : PaketPool<P> {
    @Suppress("UNCHECKED_CAST")
    private val pool = object : DefaultPool<P>(capacity) {
        override fun produceInstance() = create().also {
            it.attachToPool(this@AbstractPaketPool as PaketPool<in Paket>)
        }
        override fun validateInstance(instance: P) = instance.clear()
    }
    protected abstract fun create(): P
    final override fun produce() = pool.borrow()
    final override fun recycle(paket: P) {
        pool.recycle(paket)
    }
}

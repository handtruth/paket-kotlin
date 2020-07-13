package com.handtruth.mc.paket

import kotlinx.io.pool.DefaultPool
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface PaketSource<out P : Paket> {
    fun produce(): P
}

private object EmptyPaketSource : PaketSource<Nothing> {
    override fun produce() = throw UnsupportedOperationException()
}

fun <P : Paket> emptyPaketSource(): PaketSource<P> = EmptyPaketSource

interface PaketCreator<P : Paket> : PaketSource<P>

interface PaketPool<P : Paket> : PaketSource<P> {
    fun recycle(paket: P)
}

interface PaketSingleton<P : Paket> : PaketSource<P>

inline fun <P : Paket, R> PaketPool<P>.take(block: (P) -> R): R {
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
    private inner class RealPool(capacity: Int) : DefaultPool<P>(capacity) {
        override fun produceInstance() = create().also {
            it.attachToPool(this@AbstractPaketPool as PaketPool<in Paket>)
        }
        override fun validateInstance(instance: P) = instance.clear()
    }
    private val pool = RealPool(capacity)

    protected abstract fun create(): P
    final override fun produce() = pool.borrow()
    final override fun recycle(paket: P) {
        pool.recycle(paket)
    }
}

abstract class SinglePaket<P: SinglePaket<P>> : Paket(), PaketSingleton<P> {
    @Suppress("UNCHECKED_CAST")
    override fun produce() = this as P
}

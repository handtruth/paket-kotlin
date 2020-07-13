package com.handtruth.mc.paket.util

import com.handtruth.mc.paket.InternalPaketApi
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Closeable

@InternalPaketApi
@OptIn(ExperimentalCoroutinesApi::class)
abstract class Knot : Closeable {

    private val fiberCnt = atomic(0)
    private val entered = atomic(false)
    private val leaveCnt = atomic(0)
    protected val mutex = Mutex()

    val isActive: Boolean get() = entered.value

    private val fibersActing = atomic(0)

    private val coordinator = BroadcastChannel<Unit>(Channel.CONFLATED)

    init {
        coordinator.offer(Unit)
    }

    inner class Fiber : Closeable {

        init {
            fiberCnt.incrementAndGet()
            fibersActing.incrementAndGet()
        }

        private val node = coordinator.openSubscription()

        suspend fun start() {
            check(!isClosed)
            node.receive()
            mutex.withLock {
                if (!entered.getAndSet(true)) {
                    fibersActing.value = fiberCnt.value
                    enter()
                }
            }
        }

        suspend fun stop() {
            mutex.withLock {
                leaveCnt.incrementAndGet()
                if (leaveCnt.compareAndSet(fibersActing.value, 0)) {
                    leave()
                    leaveCnt.value = 0
                    entered.value = false
                    coordinator.offer(Unit)
                }
            }
        }

        var isClosed = false
            private set

        override fun close() {
            if (!isClosed) {
                isClosed = true
                fiberCnt.decrementAndGet()
            }
        }
    }

    protected abstract suspend fun enter()
    protected abstract suspend fun leave()

    override fun close() {
        coordinator.close()
    }

}

@InternalPaketApi
suspend inline fun <R> Knot.Fiber.weave(block: () -> R): R {
    start()
    try {
        return block()
    } finally {
        stop()
    }
}

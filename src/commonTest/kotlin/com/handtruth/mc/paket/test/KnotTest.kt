package com.handtruth.mc.paket.test

import com.handtruth.mc.paket.InternalPaketApi
import com.handtruth.mc.paket.util.Knot
import com.handtruth.mc.paket.util.weave
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.seconds

@OptIn(InternalPaketApi::class)
class KnotTest {

    class Subject : Knot() {
        private val counter = atomic(0)
        val count get() = counter.value

        private val maximum = atomic(0)
        val max get() = maximum.value

        enum class States {
            IN, OUT
        }

        private val status = atomic(States.OUT)
        val state get() = status.value

        override suspend fun enter() {
            check(state == States.OUT)
            counter.value = count + 1
            maximum.value = max(count, max)
            status.value = States.IN
            //println("ENTER")
        }

        override suspend fun leave() {
            check(state == States.IN)
            counter.value = count - 1
            status.value = States.OUT
            //println("LEAVE")
        }
    }

    class Atom {
        private val _value = atomic(0)
        fun inc() {
            _value.incrementAndGet()
        }

        val value get() = _value.value
    }

    @Test
    fun knotTest() = testTimeout(5.seconds) {
        val knot = Subject()

        val fc = 1000
        val close = 100
        val fibers = List(fc) { knot.Fiber() }
        val ohh = Atom()

        val dispatcher = Dispatchers.Default // EmptyCoroutineContext

        coroutineScope {
            repeat(fc) {
                launch(dispatcher) {
                    //println("G")
                    fibers[it].weave {
                        assertTrue(knot.isActive)
                        yield()
                        ohh.inc()
                        assertEquals(Subject.States.IN, knot.state)
                    }
                    if (it % close == 0)
                        fibers[it].close()
                }
            }
        }

        assertFalse(knot.isActive)
        assertEquals(Subject.States.OUT, knot.state, "state")
        assertEquals(1, knot.max, "maximum")
        assertEquals(fc, ohh.value, "body")
        assertEquals(0, knot.count, "left")

        println("stage 1 passed")

        val lol = Atom()

        val remains = fibers.filter { !it.isClosed }
        coroutineScope {
            repeat(10) {
                coroutineScope {
                    remains.forEach {
                        launch(dispatcher) {
                            it.weave {
                                yield()
                                lol.inc()
                            }
                        }
                    }
                }
            }
        }

        assertEquals(Subject.States.OUT, knot.state)

        //assertEquals(remains.size * 1000, lol.value, "body 2")
    }

}

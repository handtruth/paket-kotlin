package com.handtruth.mc.paket

import kotlinx.io.pool.DefaultPool
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class PaketPool<P: Paket>(val `class`: KClass<out P>, capacity: Int = 25) : DefaultPool<P>(capacity) {
    override fun produceInstance() = `class`.primaryConstructor!!.callBy(emptyMap())
    override fun validateInstance(instance: P) = instance.clear()
}

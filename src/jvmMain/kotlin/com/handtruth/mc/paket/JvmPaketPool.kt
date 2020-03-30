package com.handtruth.mc.paket

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class JvmPaketCreator<P: Paket>(private val `class`: KClass<P>) : PaketCreator<P> {
    final override fun produce() = `class`.primaryConstructor!!.callBy(emptyMap())
}

open class JvmPaketPool<P: Paket>(private val `class`: KClass<P>) : AbstractPaketPool<P>() {
    final override fun create() = `class`.primaryConstructor!!.callBy(emptyMap())
}

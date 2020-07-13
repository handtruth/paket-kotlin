package com.handtruth.mc.paket

import kotlinx.serialization.SerialInfo
import kotlin.reflect.KClass

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "Experimental features. Most likely because of experimental third party API."
)
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalPaketApi

enum class PaketSources {
    Creator, Pool, Singleton, Empty
}

@ExperimentalPaketApi
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Paketize(val source: PaketSources = PaketSources.Creator)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@SerialInfo
@MustBeDocumented
annotation class WithCodec(val codec: KClass<out Codec<*>>)

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "IntelliJ IDEA marks everything with red in tests with internal keyword, that's why this annotation " +
            "exists. You shouldn't use this API."
)
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalPaketApi

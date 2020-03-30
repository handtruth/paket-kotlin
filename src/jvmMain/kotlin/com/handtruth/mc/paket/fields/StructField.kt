package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.util.Path
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PaketConstructor

@ExperimentalPaketApi
class StructCodec<T: Any>(`class`: KClass<T>) : Codec<T> {
    private val constructor = `class`.constructors
        .find { it.findAnnotation<PaketConstructor>() != null }
        ?: `class`.primaryConstructor ?: throw IllegalProtocolStateException("Unable to find constructor for paket")

    private val fields: List<KProperty1<T, *>>
    init {
        val allProperties = `class`.memberProperties.associateBy { it.name }
        fields = constructor.parameters.asReversed().map {
            allProperties[it.name]
                ?: throw IllegalProtocolStateException("Constructor have parameters for unknown properties")
        }
    }

    // TODO: This is bad. I can't be sure that encoder is valid
    @Suppress("UNCHECKED_CAST")
    private val encoders = fields.map { getEncoder(it) as Codec<Any> }

    companion object {
        fun getEncoder(property: KProperty<*>): Codec<*> {
            val fieldMeta = property.findAnnotation<WithCodec>()
            if (fieldMeta != null)
                return fieldMeta.codec.objectInstance ?: fieldMeta.codec.primaryConstructor?.let {
                    when (it.parameters.size) {
                        0 -> it.call()
                        1 -> {
                            if (it.parameters.first().type.jvmErasure == KClass::class)
                                it.call(property.returnType.jvmErasure)
                            else
                                throw IllegalProtocolStateException(
                                    "Only class parameter supported " +
                                            "for encoder class ${property.name}"
                                )
                        }
                        else -> throw IllegalProtocolStateException("Too many parameters in encoder constructor")
                    }
                }
                ?: throw IllegalProtocolStateException("Unable to get specified encoder for property ${property.name}")
            return when (val type = property.returnType.jvmErasure) {
                Int::class -> VarIntCodec
                Boolean::class -> BoolCodec
                Byte::class -> Int8Codec
                String::class -> StringCodec
                Long::class -> VarLongCodec
                UShort::class -> UInt16Codec
                Path::class -> PathCodec
                MutableList::class, List::class -> when (val it =
                    property.returnType.arguments.first().type!!.jvmErasure) {
                    Int::class -> VarIntListCodec
                    Boolean::class -> BoolListCodec
                    Byte::class -> Int8ListCodec
                    String::class -> StringListCodec
                    Long::class -> VarLongCodec
                    UShort::class -> UInt16ListCodec
                    Path::class -> PathListCodec
                    else -> StructListCodec(it)
                }
                else -> StructCodec(type)
            }
        }
    }

    override fun measure(value: T) = encoders.asSequence().zip(fields.asSequence()).sumBy {
            (encoder, field) -> encoder.measure(field.get(value)!!)
    }

    override fun read(input: Input, old: T?) = constructor.call(
        *Array(encoders.size) { encoders[it].read(input, null) }
         .apply { reverse() }
    )

    override fun write(output: Output, value: T) = encoders.asSequence()
        .zip(fields.asSequence()).forEach { (encoder, field) ->
            encoder.write(output, field.get(value)!!)
        }
}

@ExperimentalPaketApi
class StructListCodec<T: Any>(`class`: KClass<T>) : ListCodec<T>(StructCodec(`class`))

@ExperimentalPaketApi
class StructField<T: Any>(initial: T, `class`: KClass<T>) : Field<T>(StructCodec(`class`), initial)
@ExperimentalPaketApi
class StructListField<T: Any>(initial: MutableList<T>, `class`: KClass<T>) :
    ListField<T>(StructListCodec(`class`), initial)

@ExperimentalPaketApi
inline fun <reified T: Any> Paket.struct(initial: T) = field(StructField(initial, T::class))
@ExperimentalPaketApi
inline fun <reified T: Any> Paket.listOfStruct(initial: MutableList<T>) = field(StructListField(initial, T::class))
@ExperimentalPaketApi @JvmName("listOfStructRO")
inline fun <reified T: Any> Paket.listOfStruct(initial: List<T>) = listOfStruct(initial.toMutableList())

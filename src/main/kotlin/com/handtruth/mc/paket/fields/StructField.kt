package com.handtruth.mc.paket.fields

import com.handtruth.mc.paket.*
import com.handtruth.mc.paket.util.Path
import kotlinx.io.Input
import kotlinx.io.Output
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class EncodeWith(val encoder: KClass<out Encoder<*>> = StructEncoder::class)
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PaketConstructor

class StructEncoder<T: Any>(val `class`: KClass<T>) : Encoder<T> {
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
    private val encoders = fields.map { getEncoder(it) as Encoder<Any> }

    companion object {
        fun getEncoder(property: KProperty<*>): Encoder<*> {
            val fieldMeta = property.findAnnotation<EncodeWith>()
            if (fieldMeta != null)
                return fieldMeta.encoder.objectInstance ?: fieldMeta.encoder.primaryConstructor?.let {
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
                Int::class -> VarIntEncoder
                Boolean::class -> BoolEncoder
                Byte::class -> ByteEncoder
                String::class -> StringEncoder
                Long::class -> VarLongEncoder
                UShort::class -> UInt16Encoder
                Path::class -> PathEncoder
                MutableList::class, List::class -> when (val it =
                    property.returnType.arguments.first().type!!.jvmErasure) {
                    Int::class -> VarIntListEncoder
                    Boolean::class -> BoolListEncoder
                    Byte::class -> ByteListEncoder
                    String::class -> StringListEncoder
                    Long::class -> VarLongEncoder
                    UShort::class -> UInt16ListEncoder
                    Path::class -> PathListEncoder
                    else -> StructListEncoder(it)
                }
                else -> StructEncoder(type)
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

class StructListEncoder<T: Any>(`class`: KClass<T>) : ListEncoder<T>(StructEncoder(`class`))

class StructField<T: Any>(initial: T, `class`: KClass<T>) : Field<T>(StructEncoder(`class`), initial)
class StructListField<T: Any>(initial: MutableList<T>, `class`: KClass<T>) :
    ListField<T>(StructListEncoder(`class`), initial)

inline fun <reified T: Any> Paket.struct(initial: T) = field(StructField(initial, T::class))
inline fun <reified T: Any> Paket.listOfStruct(initial: MutableList<T>) = field(StructListField(initial, T::class))
@JvmName("listOfStructRO")
inline fun <reified T: Any> Paket.listOfStruct(initial: List<T>) = listOfStruct(initial.toMutableList())

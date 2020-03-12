package com.handtruth.mc.paket

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class IllegalProtocolStateException(message: String) : IllegalStateException(message)

internal inline fun validate(value: Boolean, message: () -> Any = { "Protocol state error" }) {
    contract {
        callsInPlace(message, InvocationKind.AT_MOST_ONCE)
        returns() implies value
    }
    if (!value)
        throw IllegalProtocolStateException(message().toString())
}

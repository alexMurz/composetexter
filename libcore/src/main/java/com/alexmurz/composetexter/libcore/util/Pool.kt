package com.alexmurz.composetexter.libcore.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Not synchronized pool of typed values
 */
class Pool<T>(
    private val capacity: Int = Int.MAX_VALUE,
    private val factory: () -> T,
) {
    private val backing = mutableListOf<T>()

    fun acquire(): T {
        return backing.removeLastOrNull() ?: factory()
    }

    fun free(value: T) {
        if (backing.size < capacity) {
            backing.add(value)
        }
    }
}

@ExperimentalContracts
inline fun <T, R> Pool<T>.withValue(action: (T) -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    val value = acquire()
    val result = action(value)
    free(value)
    return result
}


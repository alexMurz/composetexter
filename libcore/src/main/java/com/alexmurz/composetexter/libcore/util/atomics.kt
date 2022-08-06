package com.alexmurz.composetexter.libcore.util

import java.util.concurrent.atomic.AtomicReference

/**
 * Update value of atomic reference and get last value before updating
 */
inline fun <T> AtomicReference<T>.getAndUpdateValue(transform: (T?) -> T?): T? {
    var a: T?
    var b: T?
    do {
        a = get()
        b = transform(a)
    } while (!compareAndSet(a, b))
    return a
}

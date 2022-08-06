package com.alexmurz.feature_core.util

inline fun Throwable.containsCause(
    condition: (Throwable) -> Boolean,
): Boolean {
    var cause = this
    do {
        if (condition(cause)) return true
        val nextCause = cause.cause
        nextCause?.let {
            cause = it
        }
    } while (nextCause != null)

    return false
}

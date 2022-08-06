package com.alexmurz.feature_core

interface ErrorHandler {
    suspend fun consumeError(): Throwable

    fun notifyError(error: Throwable)
}

inline fun <T> ErrorHandler.withErrorHandling(action: () -> T): T? = try {
    action()
} catch (e: Throwable) {
    notifyError(e)
    null
}

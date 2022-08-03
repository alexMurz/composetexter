package com.alexmurz.composetexter.apperror.service

interface AppErrorHandler {
    suspend fun consumeError(): Throwable

    fun notifyError(error: Throwable)
}

inline fun <T> AppErrorHandler.withErrorHandling(action: () -> T): T? = try {
    action()
} catch (e: Throwable) {
    notifyError(e)
    null
}

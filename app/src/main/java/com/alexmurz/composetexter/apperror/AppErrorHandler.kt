package com.alexmurz.composetexter.apperror

import com.alexmurz.feature_core.ErrorHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class AppErrorHandler : ErrorHandler {

    private val channel = Channel<Throwable>(
        capacity = Channel.RENDEZVOUS,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override suspend fun consumeError(): Throwable = channel.receive()

    override fun notifyError(error: Throwable) {
        channel.trySend(error)
    }
}
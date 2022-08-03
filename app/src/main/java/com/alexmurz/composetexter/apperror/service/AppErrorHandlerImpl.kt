package com.alexmurz.composetexter.apperror.service

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class AppErrorHandlerImpl : AppErrorHandler {

    private val channel = Channel<Throwable>(
        capacity = Channel.RENDEZVOUS,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override suspend fun consumeError(): Throwable = channel.receive()

    override fun notifyError(error: Throwable) {
        channel.trySend(error)
    }
}
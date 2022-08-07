package com.alexmurz.data.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

/**
 * Create channel receiving `T` and start listening to it applying action
 */
fun <T> runChannel(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    capacity: Int = Int.MAX_VALUE,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    action: suspend (T) -> Unit
): Pair<SendChannel<T>, Job> {

    val channel = Channel<T>(capacity, onBufferOverflow)

    val job = scope.launch {
        for (messages in channel) {
            action(messages)
        }
    }

    return channel to job
}

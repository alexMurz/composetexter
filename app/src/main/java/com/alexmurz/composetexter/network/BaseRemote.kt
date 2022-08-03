package com.alexmurz.composetexter.network

import com.alexmurz.composetexter.apperror.OperationFailedException
import com.alexmurz.composetexter.network.connectivity.ConnectivityWatcher
import com.alexmurz.composetexter.network.connectivity.awaitNetwork
import kotlinx.coroutines.withTimeout

/**
 * Utility class for any class that uses internet connectivity
 */
open class BaseRemote(
    private val connectivityWatcher: ConnectivityWatcher
) {

    protected suspend fun <T> checkGET(action: suspend () -> T): T =
        connectivityWatcher.checkGET(action)

    protected suspend fun <T> checkPOST(action: suspend () -> T): T =
        connectivityWatcher.checkPOST(action)
}

/**
 * Check for network via ConnectivityWatcher and then perform action
 * Retry `retryCount` times with given `tryTimeout`
 */
suspend inline fun <T> ConnectivityWatcher.withNetworkAndRetryCount(
    retryCount: Int,
    tryTimeout: Long = NETWORK_TIMEOUT_PER_TRY_MILLIS,
    crossinline action: suspend () -> T,
): T {
    var cause: Throwable? = null
    repeat(retryCount) {
        try {
            return withTimeout(tryTimeout) {
                awaitNetwork()
                action()
            }
        } catch (e: Exception) {
            cause = e
        }
    }
    throw OperationFailedException(
        cause = cause
    )
}

/**
 * Check network precondition for GET method
 */
suspend fun <T> ConnectivityWatcher.checkGET(action: suspend () -> T): T =
    withNetworkAndRetryCount(NETWORK_GET_RETRY_COUNT, action = action)

/**
 * Check network precondition for POST method
 */
suspend fun <T> ConnectivityWatcher.checkPOST(action: suspend () -> T): T =
    withNetworkAndRetryCount(NETWORK_POST_RETRY_COUNT, action = action)

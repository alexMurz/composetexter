package com.alexmurz.data.network

/**
 * Send then operation could not be completed
 *
 * Specifically when network GET operation completes with error after multiple attempts to
 * perform operation
 */
class OperationFailedException(
    cause: Throwable? = null
) : Exception("This operation failed with cause $cause", cause)

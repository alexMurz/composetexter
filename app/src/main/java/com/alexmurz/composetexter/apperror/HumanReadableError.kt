package com.alexmurz.composetexter.apperror

import com.alexmurz.feature_core.util.containsCause
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

abstract class HumanReadableError(
    message: String? = null,
    cause: Throwable? = null,
): Throwable(message, cause) {
    abstract val humanErrorCode: String
    abstract val humanMessage: String

    class Unknown(
        message: String? = null,
        cause: Throwable? = null,
    ) : HumanReadableError(message, cause) {
        override val humanErrorCode: String
            get() = "?"

        override val humanMessage: String
            get() = "Unknown error"
    }

    class NotSupportedOperation(
        message: String? = null,
        cause: Throwable? = null,
    ) : HumanReadableError(message, cause) {
        override val humanErrorCode: String
            get() = "?"

        override val humanMessage: String
            get() = "Not supported operation"
    }

    class Timeout(
        message: String? = null,
        cause: Throwable? = null,
    ) : HumanReadableError(message, cause) {
        override val humanErrorCode: String
            get() = "?"

        override val humanMessage: String
            get() = "Timeout exception"
    }

    class Server(
        message: String? = null,
        cause: Throwable? = null
    ) : HumanReadableError(message, cause) {
        override val humanErrorCode: String
            get() = "?"

        override val humanMessage: String
            get() = "Server error ${cause?.message}"
    }

    companion object {

        fun isTimeoutKind(e: Throwable): Boolean = e.containsCause {
            when (it) {
                is TimeoutException,
                is TimeoutCancellationException,
                is SocketTimeoutException,
                is InterruptedIOException -> true
                else -> false
            }
        }

        fun isNotSupportedKind(e: Throwable): Boolean = e.containsCause {
            when (it) {
                is NotImplementedError -> true
                else -> false
            }
        }

        fun isServerErrorKind(e: Throwable): Boolean = e.containsCause {
            when (it) {
                is HttpException -> it.code() in 500..599
                else -> false
            }
        }

        fun parseThrowableOrUnknown(throwable: Throwable): HumanReadableError =
            parseThrowable(throwable) ?: Unknown()

        fun parseThrowable(throwable: Throwable): HumanReadableError? = when {
            throwable is HumanReadableError -> throwable
            isServerErrorKind(throwable) -> Server(cause = throwable)
            isTimeoutKind(throwable) -> Timeout(cause = throwable)
            isNotSupportedKind(throwable) -> NotSupportedOperation(cause = throwable)
            else -> null
        }
    }
}



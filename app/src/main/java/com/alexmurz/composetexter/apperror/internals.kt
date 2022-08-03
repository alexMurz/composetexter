package com.alexmurz.composetexter.apperror

import androidx.compose.runtime.Immutable

internal const val ERROR_VISIBILITY_DURATION_MILLIS: Long = 10_000L

@Immutable
class AppErrorWrapper(
    val error: HumanReadableError,
    val seqId: Int,
)

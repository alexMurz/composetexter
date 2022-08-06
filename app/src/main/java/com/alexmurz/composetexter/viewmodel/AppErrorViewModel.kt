package com.alexmurz.composetexter.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmurz.composetexter.apperror.AppErrorWrapper
import com.alexmurz.composetexter.apperror.ErrorHandler
import com.alexmurz.composetexter.apperror.HumanReadableError
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicInteger

class AppErrorViewModel : ViewModel(), KoinComponent {
    private val handler by inject<ErrorHandler>()

    private var seqId = AtomicInteger(0)

    val errorList = mutableStateOf<List<AppErrorWrapper>>(emptyList())

    private inline fun applyErrorList(action: MutableList<AppErrorWrapper>.() -> Boolean) {
        val list = errorList.value.toMutableList()
        if (action(list)) {
            errorList.value = list
        }
    }

    init {
        viewModelScope.launch {
            while (isActive) {
                processThrowable(handler.consumeError())
            }
        }
    }

    private fun processThrowable(throwable: Throwable) {
        val error = HumanReadableError.parseThrowable(throwable) ?: throw Throwable(
            "Failed to process as HumanReadableError, most likely fatal error",
            throwable
        )

        error.printStackTrace()

        val wrapper = AppErrorWrapper(
            error = error,
            seqId = seqId.getAndIncrement(),
        )
        applyErrorList {
            add(wrapper)
        }
    }

    fun onErrorDismiss(error: AppErrorWrapper) {
        applyErrorList {
            remove(error)
        }
    }

}
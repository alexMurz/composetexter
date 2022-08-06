package com.alexmurz.composetexter.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.alexmurz.composetexter.apperror.AppErrorWrapper
import com.alexmurz.composetexter.apperror.ErrorHandler
import com.alexmurz.composetexter.apperror.HumanReadableError
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicInteger

class AppErrorViewModel : ViewModel(), KoinComponent {
    private val relay by inject<ErrorHandler>()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private var seqId = AtomicInteger(0)

    val errorList = mutableStateOf<List<AppErrorWrapper>>(emptyList())

    private inline fun applyErrorList(action: MutableList<AppErrorWrapper>.() -> Boolean) {
        synchronized(errorList) {
            val list = errorList.value.toMutableList()
            if (action(list)) {
                errorList.value = list
            }
        }
    }

    init {
        scope.launch {
            while (isActive) {
                val throwable = relay.consumeError()
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.complete()
    }

    fun onErrorDismiss(error: AppErrorWrapper) {
        applyErrorList {
            remove(error)
        }
    }

}
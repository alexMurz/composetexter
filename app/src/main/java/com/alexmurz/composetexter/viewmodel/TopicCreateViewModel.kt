package com.alexmurz.composetexter.viewmodel

import androidx.lifecycle.ViewModel
import com.alexmurz.composetexter.apperror.ErrorHandler
import com.alexmurz.composetexter.apperror.withErrorHandling
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.service.TopicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TopicCreateFieldState(
    val value: String,
    val state: FieldState
) {
    enum class FieldState {
        Ok
    }
}


private fun field(
    value: String = "",
    state: TopicCreateFieldState.FieldState = TopicCreateFieldState.FieldState.Ok,
) = TopicCreateFieldState(value, state)

class TopicCreateViewModel : ViewModel(), KoinComponent {

    private val errorHandler by inject<ErrorHandler>()
    private val service by inject<TopicService>()

    private val mTitleField = MutableStateFlow(field())
    private val mMessageField = MutableStateFlow(field())

    val titleFieldStateFlow: StateFlow<TopicCreateFieldState>
        get() = mTitleField

    val messageFieldStateFlow: StateFlow<TopicCreateFieldState>
        get() = mMessageField

    fun onTitleChanged(newTitle: String) {
        mTitleField.value = field(value = newTitle)
    }

    fun onMessageChanged(newTitle: String) {
        mMessageField.value = field(value = newTitle)
    }

    suspend fun createTopic(): Topic? {
        val title = mTitleField.value.value
        val message = mMessageField.value.value
        val topic = errorHandler.withErrorHandling {
            service.createTopic(
                title = title,
                message = message
            )
        }

        return topic?.also {
            mTitleField.value = field()
            mMessageField.value = field()
        }
    }
}
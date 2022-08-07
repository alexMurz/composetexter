package com.alexmurz.composetexter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MessageListViewModel(
    private val parent: MessageChainParent
) : ViewModel(), KoinComponent {

    private val mItems = MutableStateFlow(emptyList<MessageViewModel>())

    val messages: StateFlow<List<MessageViewModel>>
        get() = mItems

    init {
        viewModelScope.launch {
            mItems.value = listOf(
                MessageViewModel(
                    message = Message(
                        id = 0,
                        message = "Message Message Message Message Message Message Message Message Message Message Message",
                        dateCreated = CATime.now(),
                        dateUpdated = CATime.now(),
                    ),
                    isLeft = true,
                    isEndOfChain = true,
                ),
            )
        }
    }
}

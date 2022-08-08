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
import java.util.concurrent.atomic.AtomicInteger

class MessageListViewModel(
    private val parent: MessageChainParent
) : ViewModel(), KoinComponent {

    private val mItems = MutableStateFlow(emptyList<MessageViewModel>())

    val messages: StateFlow<List<MessageViewModel>>
        get() = mItems

    init {
        val idCounter = AtomicInteger(0)
        fun message(
            text: String
        ): MessageViewModel {
            val id = idCounter.getAndIncrement()
            return MessageViewModel(
                message = Message(
                    id = id,
                    message = text,
                    dateCreated = CATime.now(),
                    dateUpdated = CATime.now(),
                ),
                isLeft = (id % 4) == 0,
                isEndOfChain = (id % 2) == 1,
            )
        }

        viewModelScope.launch {
            mItems.value = (0 until 20).map {
                val r = (Math.random() * 10).toInt()
                message("Message $it. " + "123 ".repeat(r))
            }
        }
    }
}

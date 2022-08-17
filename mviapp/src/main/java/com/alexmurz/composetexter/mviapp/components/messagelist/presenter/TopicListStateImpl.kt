package com.alexmurz.composetexter.mviapp.components.messagelist.presenter

import com.alexmurz.composetexter.mviapp.components.messagelist.MessageListState
import com.alexmurz.messages.model.Message

internal class MessageListStateImpl(
    override val messages: List<Message>
) : MessageListState

package com.alexmurz.composetexter.viewmodel

import com.alexmurz.messages.model.Message

class MessageViewModel(
    val message: Message,
    val isLeft: Boolean,
    val isEndOfChain: Boolean,
)

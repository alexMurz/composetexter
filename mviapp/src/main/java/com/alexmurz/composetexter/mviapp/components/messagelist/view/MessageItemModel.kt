package com.alexmurz.composetexter.mviapp.components.messagelist.view

import com.alexmurz.messages.model.Message

class MessageItemModel(
    val message: Message,
    val onClickListener: ((Message) -> Unit)? = null,
)

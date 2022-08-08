package com.alexmurz.composetexter.ui

import androidx.compose.runtime.Composable
import com.alexmurz.composetexter.ui.message.MessageList
import com.alexmurz.messages.model.MessageChainParent

@Composable
fun FeedScreen(parent: MessageChainParent) {
    MessageList(parent = parent)
}

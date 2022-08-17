package com.alexmurz.composetexter.mviapp.nav_host

import com.alexmurz.messages.model.MessageChainParent

sealed class Destination {
    object TopicList : Destination() {
        override fun toString(): String = "TopicList"
    }

    data class MessageList(val messageParent: MessageChainParent) : Destination()
}

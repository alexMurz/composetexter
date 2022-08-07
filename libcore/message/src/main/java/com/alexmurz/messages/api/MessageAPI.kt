package com.alexmurz.messages.api

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent

interface MessageAPI {
    fun interface LoadNewest {
        suspend fun loadNewest(limit: Int, parent: MessageChainParent): Set<Message>
    }

    fun interface LoadNewer {
        suspend fun loadNewer(limit: Int, parent: MessageChainParent, date: CATime): Set<Message>
    }

    fun interface LoadOlder {
        suspend fun loadOlder(limit: Int, parent: MessageChainParent, date: CATime): Set<Message>
    }

    fun interface Save {
        suspend fun save(parent: MessageChainParent, messages: Iterable<Message>)
    }

    fun interface Post {
        suspend fun postMessage(text: String): Message
    }
}

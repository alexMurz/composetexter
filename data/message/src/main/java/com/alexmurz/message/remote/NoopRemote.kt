package com.alexmurz.message.remote

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.messages.api.MessageAPI
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent

internal object NoopRemote :
    MessageAPI.LoadNewest,
    MessageAPI.LoadNewer,
    MessageAPI.LoadOlder,
    MessageAPI.Post {
    override suspend fun loadNewest(limit: Int, parent: MessageChainParent): Set<Message> = emptySet()

    override suspend fun loadNewer(
        limit: Int,
        parent: MessageChainParent,
        date: CATime
    ): Set<Message> = emptySet()

    override suspend fun loadOlder(
        limit: Int,
        parent: MessageChainParent,
        date: CATime
    ): Set<Message> = emptySet()

    override suspend fun postMessage(text: String): Message {
        throw NotImplementedError("Noop remote cannot post message")
    }
}
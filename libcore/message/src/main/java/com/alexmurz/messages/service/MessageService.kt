package com.alexmurz.messages.service

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.libcore.service.set_service.CommonSetBasedService
import com.alexmurz.messages.api.MessageAPI
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent

private inline val Message.referenceDate: CATime
    get() = dateCreated

class MessageService(
    private val localLoadNewest: MessageAPI.LoadNewest,
    private val localLoadOlder: MessageAPI.LoadOlder,
    private val localSave: MessageAPI.Save,
    private val remoteLoadNewest: MessageAPI.LoadNewest,
    private val remoteLoadNewer: MessageAPI.LoadNewer,
    private val remoteLoadOlder: MessageAPI.LoadOlder,
    private val remotePost: MessageAPI.Post,
) : CommonSetBasedService<Message, MessageServiceContext>() {
    override suspend fun localLoadNewest(context: MessageServiceContext): Set<Message> =
        localLoadNewest.loadNewest(context.limit, context.parent)

    override suspend fun localLoadOlder(
        context: MessageServiceContext,
        reference: Message
    ): Set<Message> =
        localLoadOlder.loadOlder(context.limit, context.parent, reference.referenceDate)

    override suspend fun localSave(context: MessageServiceContext, data: Set<Message>) =
        localSave.save(context.parent, data)

    override suspend fun remoteLoadNewest(context: MessageServiceContext): Set<Message> =
        remoteLoadNewest.loadNewest(context.limit, context.parent)

    override suspend fun remoteLoadNewer(
        context: MessageServiceContext,
        reference: Message
    ): Set<Message> =
        remoteLoadNewer.loadNewer(context.limit, context.parent, reference.referenceDate)

    override suspend fun remoteLoadOlder(
        context: MessageServiceContext,
        reference: Message
    ): Set<Message> =
        remoteLoadOlder.loadOlder(context.limit, context.parent, reference.referenceDate)

    suspend fun postMessage(parent: MessageChainParent, text: String): Message =
        remotePost.postMessage(text).also {
            localSave.save(parent, setOf(it))
        }
}

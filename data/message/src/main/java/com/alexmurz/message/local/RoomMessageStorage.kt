package com.alexmurz.message.local

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.data.util.runChannel
import com.alexmurz.message.dao.MessageDao
import com.alexmurz.message.mapper.MessageEntityMapper
import com.alexmurz.message.model.MessageEntity
import com.alexmurz.messages.api.MessageAPI
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel

class RoomMessageStorage(
    private val dao: MessageDao,
    private val mapper: MessageEntityMapper,
) : MessageAPI.LoadNewest,
    MessageAPI.LoadNewer,
    MessageAPI.LoadOlder,
    MessageAPI.Save {

    private val save = runChannel<List<MessageEntity>> {
        dao.save(it)
    }.first

    @Suppress("NOTHING_TO_INLINE")
    private inline fun List<MessageEntity>.mapEntities(): Set<Message> =
        mapTo(mutableSetOf(), mapper::fromEntity)

    override suspend fun loadNewest(limit: Int, parent: MessageChainParent): Set<Message> =
        dao.newest(parent.packedId, limit).mapEntities()

    override suspend fun loadNewer(
        limit: Int,
        parent: MessageChainParent,
        date: CATime
    ): Set<Message> = dao.loadNewer(parent.packedId, date.timestamp, limit).mapEntities()

    override suspend fun loadOlder(
        limit: Int,
        parent: MessageChainParent,
        date: CATime
    ): Set<Message> = dao.loadOlder(parent.packedId, date.timestamp, limit).mapEntities()

    override suspend fun save(parent: MessageChainParent, messages: Iterable<Message>) =
        save.send(messages.map { mapper.toEntity(parent, it) })

}
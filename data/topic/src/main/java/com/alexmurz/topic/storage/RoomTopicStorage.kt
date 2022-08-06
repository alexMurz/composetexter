package com.alexmurz.topic.storage

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.data.util.AndroidLoggable
import com.alexmurz.data.util.Loggable
import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class RoomTopicStorage(
    private val dao: TopicDao,
    private val mapper: TopicEntityMapper,
) : TopicAPI.LoadNewest,
    TopicAPI.LoadUp,
    TopicAPI.LoadDown,
    TopicAPI.SaveTopics,
    Loggable by AndroidLoggable("RoomTopicStorage") {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val saveChannel = Channel<Set<Topic>>(Int.MAX_VALUE)

    init {
        scope.launch {
            readSaveChannel()
        }
    }

    private suspend fun readSaveChannel() {
        for (set in saveChannel) {
            val entities = set.map(mapper::toEntity)
            dao.saveTopics(entities)
        }
    }

    override suspend fun loadNewestTopics(limit: Int): Set<Topic> {
        return dao.getNewestTopics(limit).mapTo(mutableSetOf(), mapper::fromEntity)
    }

    override suspend fun loadDownTopics(date: CATime, limit: Int): Set<Topic> {
        log("loadDownTopics(date=$date, limit=$limit) start ...")
        return dao.loadDown(date.timestamp, limit).mapTo(mutableSetOf(), mapper::fromEntity).also {
            log("loadDownTopics(date=$date, limit=$limit) complete")
        }
    }

    override suspend fun loadUpTopics(date: CATime, limit: Int): Set<Topic> {
        return dao.loadUp(date.timestamp, limit).mapTo(mutableSetOf(), mapper::fromEntity)
    }

    override suspend fun saveTopics(topics: Set<Topic>) {
        scope.launch {
            println("Send to save channel")
            saveChannel.send(topics)
            println("Did Send to save channel")
        }
    }
}
package com.alexmurz.topic.actions

import com.alexmurz.topic.TopicAction
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.CreateTopicRequest
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.remote.TopicRemote
import com.alexmurz.topic.service.TopicServiceContext

internal class CreateTopicImpl(
    private val mapper: TopicEntityMapper,
    private val dao: TopicDao,
    private val remote: TopicRemote,
) : TopicAction.CreateTopic {

    override suspend fun createTopic(
        context: TopicServiceContext,
        createTopicRequest: CreateTopicRequest
    ): Topic = remote.createTopic(
        title = createTopicRequest.title,
        message = createTopicRequest.message,
    ).also { topic ->
        // Save to DB
        val topicEntity = mapper.toEntity(topic)
        dao.saveTopics(listOf(topicEntity))
        // Save to Hot memory
        context.addItems(listOf(topic))
    }
}
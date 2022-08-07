package com.alexmurz.topic.service

import com.alexmurz.composetexter.libcore.service.set_service.CommonSetBasedService
import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.model.Topic

/**
 * Service for interaction with topics
 */
class TopicService(
    private val remoteLoadDown: TopicAPI.LoadOlder,
    private val remoteLoadUp: TopicAPI.LoadNewer,
    private val remoteLoadNewest: TopicAPI.LoadNewest,
    private val localLoadDownPage: TopicAPI.LoadOlder,
    private val localLoadNewest: TopicAPI.LoadNewest,
    private val localSaveTopics: TopicAPI.SaveTopics,
    private val createNewTopic: TopicAPI.CreateTopic,
) : CommonSetBasedService<Topic, TopicServiceContext>() {

    override suspend fun localLoadNewest(context: TopicServiceContext): Set<Topic> =
        localLoadNewest.loadNewestTopics(context.limit)

    override suspend fun localLoadOlder(
        context: TopicServiceContext,
        reference: Topic
    ): Set<Topic> =
        localLoadDownPage.loadDownTopics(reference.date, context.limit)

    override suspend fun localSave(context: TopicServiceContext, data: Set<Topic>) =
        localSaveTopics.saveTopics(data)

    override suspend fun remoteLoadNewest(context: TopicServiceContext): Set<Topic> =
        remoteLoadNewest.loadNewestTopics(context.limit)

    override suspend fun remoteLoadNewer(
        context: TopicServiceContext,
        reference: Topic
    ): Set<Topic> = remoteLoadUp.loadUpTopics(reference.date, context.limit)

    override suspend fun remoteLoadOlder(
        context: TopicServiceContext,
        reference: Topic
    ): Set<Topic> = remoteLoadDown.loadDownTopics(reference.date, context.limit)

    /**
     * Create new empty content with given parameters
     */
    fun createNewContext(limit: Int): TopicServiceContext {
        return TopicServiceContext(
            limit = limit,
        )
    }

    suspend fun createTopic(
        title: String,
        message: String
    ): Topic = createNewTopic.createTopic(title, message).also { topic ->
        localSaveTopics.saveTopics(setOf(topic))
    }
}

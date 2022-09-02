package com.alexmurz.topic.service

import com.alexmurz.topic.TopicAction
import com.alexmurz.topic.model.CreateTopicRequest
import com.alexmurz.topic.model.Topic

class TopicService(
    private val initialize: TopicAction.Initialize,
    private val update: TopicAction.Update,
    private val loadMore: TopicAction.LoadMore,
    private val createTopic: TopicAction.CreateTopic
) {
    suspend fun initialize(context: TopicServiceContext): Set<Topic> =
        initialize.initialize(context)

    suspend fun update(context: TopicServiceContext): Set<Topic> =
        update.update(context)

    suspend fun loadMore(context: TopicServiceContext): Set<Topic> =
        loadMore.loadMore(context)

    suspend fun createTopic(context: TopicServiceContext, createTopicRequest: CreateTopicRequest) =
        createTopic.createTopic(context, createTopicRequest)
}

package com.alexmurz.topic.service

import com.alexmurz.topic.TopicUseCase
import com.alexmurz.topic.model.CreateTopicRequest
import com.alexmurz.topic.model.Topic

class TopicService(
    private val initialize: TopicUseCase.Initialize,
    private val update: TopicUseCase.Update,
    private val loadMore: TopicUseCase.LoadMore,
    private val createTopic: TopicUseCase.CreateTopic
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

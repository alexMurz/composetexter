package com.alexmurz.topic

import com.alexmurz.topic.model.CreateTopicRequest
import com.alexmurz.topic.model.Topic

/**
 * User use cases of topic module
 */
interface TopicUseCase {
    /**
     * User first look at topic list
     */
    class Initialize(
        private val initialize: TopicAction.Initialize,
    ) {
        suspend fun initialize(context: TopicsContext): Set<Topic> =
            initialize.initialize(context)
    }

    /**
     * User updates topic list
     */
    class Update(
        private val update: TopicAction.Update,
    ) {
        suspend fun update(context: TopicsContext): Set<Topic> =
            update.update(context)
    }

    /**
     * User scrolls to the end of the list and wants to see more topics
     */
    class LoadMore(
        private val loadMore: TopicAction.LoadMore,
    ) {
        suspend fun loadMore(context: TopicsContext): Set<Topic> =
            loadMore.loadMore(context)
    }

    /**
     * User requests to create topic with given descriptor
     */
    class CreateTopic(
        private val createTopic: TopicAction.CreateTopic,
    ) {
        suspend fun createTopic(context: TopicsContext, request: CreateTopicRequest): Topic =
            createTopic.createTopic(context, request)
    }
}
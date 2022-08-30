package com.alexmurz.topic

import com.alexmurz.topic.model.CreateTopicRequest
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.service.TopicServiceContext

interface TopicUseCase {
    /**
     * Initialize context from local data if available
     */
    interface Initialize {
        suspend fun initialize(context: TopicServiceContext): Set<Topic>
    }

    /**
     * Update topics, loading newer topics
     */
    interface Update {
        suspend fun update(context: TopicServiceContext): Set<Topic>
    }

    /**
     * Load more older topics
     */
    interface LoadMore {
        suspend fun loadMore(context: TopicServiceContext): Set<Topic>
    }

    /**
     * Create new topic
     */
    interface CreateTopic {
        suspend fun createTopic(
            context: TopicServiceContext,
            createTopicRequest: CreateTopicRequest
        ): Topic
    }
}

package com.alexmurz.topic

import com.alexmurz.topic.model.CreateTopicRequest
import com.alexmurz.topic.model.Topic

interface TopicAction {
    /**
     * Initialize context from local data if available
     */
    interface Initialize {
        suspend fun initialize(context: TopicsContext): Set<Topic>
    }

    /**
     * Update topics, loading newer topics
     */
    interface Update {
        suspend fun update(context: TopicsContext): Set<Topic>
    }

    /**
     * Load more older topics
     */
    interface LoadMore {
        suspend fun loadMore(context: TopicsContext): Set<Topic>
    }

    /**
     * Create new topic
     */
    interface CreateTopic {
        suspend fun createTopic(
            context: TopicsContext,
            createTopicRequest: CreateTopicRequest
        ): Topic
    }
}

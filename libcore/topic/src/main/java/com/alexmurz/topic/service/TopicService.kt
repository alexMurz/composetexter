package com.alexmurz.topic.service

import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.model.Topic

/**
 * Service for interaction with topics
 */
class TopicService(
    private val remoteLoadDown: TopicAPI.LoadDown,
    private val remoteLoadUp: TopicAPI.LoadUp,
    private val remoteLoadNewest: TopicAPI.LoadNewest,
    private val localLoadDownPage: TopicAPI.LoadDown,
    private val localLoadNewest: TopicAPI.LoadNewest,
    private val localSaveTopics: TopicAPI.SaveTopics,
    private val createNewTopic: TopicAPI.CreateTopic,
) {
    /**
     * Create new empty content with given parameters
     */
    fun createNewContext(limit: Int): TopicServiceContext {
        return TopicServiceContext(
            limit = limit,
            initialTopics = emptySet(),
            upToDate = false,
            hasMoreLocal = true,
            hasMoreRemote = true,
        )
    }

    suspend fun updateTopics(context: TopicServiceContext): Set<Topic> {
        val limit = context.limit

        val topics = when (val newestTopic = context.topics.maxByOrNull { it.date }) {
            null -> remoteLoadNewest.loadNewestTopics(limit)
            else -> remoteLoadUp.loadUpTopics(newestTopic.date, limit)
        }

        context.upToDate = topics.isEmpty()
        if (topics.isNotEmpty()) {
            localSaveTopics.saveTopics(topics)
            context.addTopics(topics)
        }

        return topics
    }

    suspend fun loadMoreTopics(context: TopicServiceContext): Set<Topic> {
        if (!context.hasMoreLocal && !context.hasMoreRemote) return emptySet()

        val limit = context.limit
        val oldestTopic = context.topics.minByOrNull { it.date }

        val topicsFromDatabase = when {
            !context.hasMoreLocal -> emptySet()
            oldestTopic == null -> localLoadNewest.loadNewestTopics(limit)
            else -> localLoadDownPage.loadDownTopics(oldestTopic.date, limit)
        }

        if (topicsFromDatabase.isNotEmpty()) {
            context.addTopics(topicsFromDatabase)
            return topicsFromDatabase
        }

        /// Not enough topics in database, set flag that database is exhausted
        context.hasMoreLocal = false

        val topicsFromRemote = when {
            !context.hasMoreRemote -> emptySet()
            oldestTopic == null -> remoteLoadNewest.loadNewestTopics(limit)
            else -> remoteLoadDown.loadDownTopics(oldestTopic.date, limit)
        }

        if (topicsFromRemote.isEmpty()) {
            context.hasMoreRemote = false
        } else {
            localSaveTopics.saveTopics(topicsFromRemote)
        }

        context.addTopics(topicsFromRemote)
        return topicsFromRemote
    }

    suspend fun createTopic(
        context: TopicServiceContext,
        title: String,
        message: String
    ): Topic = createNewTopic.createTopic(title, message).also { topic ->
        val set = setOf(topic)
        context.addTopics(set)
        localSaveTopics.saveTopics(set)
    }
}

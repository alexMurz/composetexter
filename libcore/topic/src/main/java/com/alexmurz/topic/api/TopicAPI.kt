package com.alexmurz.topic.api

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.model.Topic

interface TopicAPI {

    interface LoadNewest {
        /**
         * Get newest `limit` topics
         */
        suspend fun loadNewestTopics(limit: Int): Set<Topic>
    }

    interface LoadDown {
        /**
         * Get topics starting from `date` and take up to `limit` older topics
         * Exclude topic at exact date
         */
        suspend fun loadDownTopics(date: CATime, limit: Int): Set<Topic>
    }

    interface LoadUp {
        /**
         * Get new topics starting from `date` up to `limit`
         * Exclude topic at exact date
         */
        suspend fun loadUpTopics(date: CATime, limit: Int): Set<Topic>
    }

    interface SaveTopics {
        suspend fun saveTopics(topics: Set<Topic>)
    }

    interface CreateTopic {
        suspend fun createTopic(title: String, message: String, ): Topic
    }

}
package com.alexmurz.topic.service

import com.alexmurz.topic.model.Topic
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicReference

/**
 * Hot cache and context holder for `TopicService`
 */
class TopicServiceContext internal constructor(
    /**
     * Size of single `pack`
     */
    val limit: Int,
    initialTopics: Set<Topic>,
    upToDate: Boolean,
    hasMoreLocal: Boolean,
    hasMoreRemote: Boolean
) {

    private val topicsHolder = AtomicReference(initialTopics)


    // Hot cached topics
    val topics: Set<Topic>
        get() = topicsHolder.get()

    // True then all updates complete
    var upToDate = upToDate
        internal set

    // True is database data is not exhausted
    var hasMoreLocal = hasMoreLocal
        internal set

    // True is remote data is not exhausted
    var hasMoreRemote = hasMoreRemote
        internal set

    /**
     * Add topics to list of topics
     */
    fun addTopics(items: Set<Topic>) {
        var oldList: Set<Topic>
        var newList: Set<Topic>
        do {
            oldList = topicsHolder.get()
            newList = oldList + items
        } while (!topicsHolder.compareAndSet(oldList, newList))
    }
}

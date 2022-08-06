package com.alexmurz.feature_topics.viewmodel

import androidx.lifecycle.ViewModel
import com.alexmurz.composetexter.libcore.util.then
import com.alexmurz.feature_core.ErrorHandler
import com.alexmurz.feature_core.util.AndroidLoggable
import com.alexmurz.feature_core.util.Loggable
import com.alexmurz.feature_core.withErrorHandling
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.service.TopicService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val LIMIT = 25

private val topicSortComparator = Comparator<Topic> { a, b ->
    a.order.compareTo(b.order)
}

private inline fun <T> MutableStateFlow<Boolean>.withFlag(
    expect: Boolean = false,
    update: Boolean = true,
    action: () -> T
): T? = compareAndSet(expect, update).then {
    action().also {
        value = false
    }
}

class TopicListViewModel :
    ViewModel(),
    KoinComponent,
    Loggable by AndroidLoggable("TopicListViewModel", enabled = true) {

    private val errorRelay by inject<ErrorHandler>()
    private val service by inject<TopicService>()
    private val context by lazy { service.createNewContext(LIMIT) }

    private val mIsUpdating = MutableStateFlow(false)
    private val mIsLoadingMore = MutableStateFlow(false)

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val topics = MutableStateFlow(emptyList<Topic>())

    val content: StateFlow<List<Topic>>
        get() = topics

    val isUpdating: StateFlow<Boolean>
        get() = mIsUpdating

    val isLoadingMore: StateFlow<Boolean>
        get() = mIsLoadingMore

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    @Synchronized
    private fun addTopics(topics: Iterable<Topic>) {
        val newTopicIds = topics.map { it.id }
        val newList = this.topics.value.toMutableList()
        newList.removeAll { it.id in newTopicIds }
        newList.addAll(topics)
        newList.sortWith(topicSortComparator)
        this.topics.value = newList
    }

    // Run action to get new topics with error handling
    private inline fun runForTopics(action: () -> Iterable<Topic>) {
        errorRelay.withErrorHandling(action)?.let(this::addTopics)
    }

    fun update() {
        log("update - Start")
        scope.launch {
            mIsUpdating.withFlag {
                log("update - get and add topics ...")
                runForTopics { service.updateTopics(context) }
            } ?: log("update - skipped, busy")
            log("update - complete")
        }
    }

    // Load down
    fun loadMore() {
        log("loadMore - Start, check context")
        if (topics.value.isEmpty()) {
            log("loadMore - ignored, no topics, need to update first")
            return
        }

        // Load more only works if context initialized
        scope.launch {
            mIsLoadingMore.withFlag {
                log("loadMore - get and add topics ...")
                runForTopics { service.loadMoreTopics(context) }
            } ?: log("loadMore - skipped, busy")

            log("loadMore - complete")
        }
    }
}

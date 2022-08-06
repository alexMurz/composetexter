package com.alexmurz.composetexter.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.composetexter.viewmodel.TopicListViewModel
import com.alexmurz.topic.model.Topic
import java.util.concurrent.atomic.AtomicBoolean

class TopicListState(
    internal val viewModel: TopicListViewModel,
    internal val lazyListState: LazyListState,
    internal val initializeViewModel: AtomicBoolean,
) {
    fun onTopicCreated(topic: Topic) {
        val topics = setOf(topic)
        viewModel.context.addTopics(topics)
        viewModel.addTopics(topics)
    }
}

@Composable
fun rememberTopicListState(): TopicListState {
    val viewModel: TopicListViewModel = viewModel()
    val lazyListState = rememberLazyListState()

    return remember {
        TopicListState(
            viewModel = viewModel,
            lazyListState = lazyListState,
            initializeViewModel = AtomicBoolean(true)
        )
    }
}

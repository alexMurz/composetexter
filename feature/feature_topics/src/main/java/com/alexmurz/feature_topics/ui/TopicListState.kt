package com.alexmurz.feature_topics.ui

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.feature_topics.viewmodel.TopicListViewModel
import java.util.concurrent.atomic.AtomicBoolean

class TopicListState(
    internal val viewModel: TopicListViewModel,
    internal val lazyListState: LazyListState,
    internal val initializeViewModel: AtomicBoolean,
) {

    /**
     * Request update list
     *
     * Should be used after creating new item
     */
    fun updateList() {
        initializeViewModel.set(true)
    }
}

@Composable
fun rememberTopicListState(): TopicListState {
    val viewModel: TopicListViewModel = viewModel()
    val lazyListState = rememberLazyListState()

    return remember {
        Log.i("AAAQQQ", "Create state")
        TopicListState(
            viewModel = viewModel,
            lazyListState = lazyListState,
            initializeViewModel = AtomicBoolean(true)
        )
    }
}

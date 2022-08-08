package com.alexmurz.composetexter.ui.topic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexmurz.composetexter.effect.LazyListLoadMoreEffect
import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.model.Topic
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

private const val LOADING_THRESHOLD = 5

// List keys
private const val NO_CONTENT = "NO_CONTENT"
private const val PROGRESS_LOADER = "PROGRESS"

@Composable
fun TopicList(
    state: TopicListState = rememberTopicListState(),
    onTopicClicked: ((Topic) -> Unit)? = null,
) {
    val viewModel = state.viewModel
    val listState = state.lazyListState

    val content by viewModel.content.collectAsState(initial = emptyList())

    val isUpdating by viewModel.isUpdating.collectAsState(initial = false)
    val isLoadingMore by viewModel.isLoadingMore.collectAsState(initial = false)

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isUpdating
    )

    // Initial update
    LaunchedEffect(viewModel) {
        if (state.initializeViewModel.compareAndSet(true, false)) {
            viewModel.update()
        }
    }

    LazyListLoadMoreEffect(
        lazyListState = listState,
        threshold = LOADING_THRESHOLD
    ) {
        viewModel.loadMore()
    }

    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = swipeRefreshState,
        onRefresh = viewModel::update,
    ) {
        LazyColumn(
            modifier = Modifier,
            state = listState,
        ) {
            if (content.isEmpty()) {
                item(key = NO_CONTENT) {
                    NoContent()
                }
            } else {
                items(
                    count = content.size,
                    key = { content[it].id },
                    itemContent = {
                        val bottomSeparator = it < content.size - 1
                        val topic = content[it]
                        TopicView(
                            topic = topic,
                            bottomSeparator = bottomSeparator,
                            onClick = { onTopicClicked?.invoke(topic) }
                        )
                    },
                )

                item(key = PROGRESS_LOADER) {
                    AnimatedVisibility(visible = isLoadingMore) {
                        ProgressLoader()
                    }
                }
            }
        }
    }
}

@Composable
private fun NoContent() {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "TODO: NO CONTENT",
            modifier = Modifier
                .align(Alignment.Center),
        )

    }
}

@Composable
private fun ProgressLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth()

    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun Preview() {

    val content = listOf(
        Topic(1, CATime.now(), "1", "", emptyList()),
        Topic(2, CATime.now(), "2", "", emptyList()),
        Topic(3, CATime.now(), "3", "", emptyList()),
        Topic(4, CATime.now(), "4", "", emptyList()),
        Topic(5, CATime.now(), "5", "", emptyList()),
    )
    LazyColumn {
        items(content.size) {
            TopicView(content[it])
        }
    }
}

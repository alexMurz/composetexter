package com.alexmurz.composetexter.modules.topic.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.modules.topic.ui.topic.TopicView
import com.alexmurz.composetexter.modules.topic.viewmodel.TopicListViewModel
import com.alexmurz.composetexter.util.compose_ext.LazyListLoadMoreEffect
import com.alexmurz.topic.model.Topic
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.util.concurrent.atomic.AtomicInteger

private const val LOADING_THRESHOLD = 5

private val aaa = AtomicInteger(0)

@Composable
fun TopicList(
    viewModel: TopicListViewModel = viewModel()
) {
    val content by viewModel.content.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()

    val isUpdating by viewModel.isUpdating.collectAsState(initial = false)
    val isLoadingMore by viewModel.isLoadingMore.collectAsState(initial = false)

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isUpdating
    )

    // Initial update
    LaunchedEffect(viewModel) {
        viewModel.update()
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
            item {
                Button(onClick = viewModel::create) {
                    Text("Create topic")
                }
            }

            items(content.size) {
                val bottomSeparator = it < content.size - 1
                TopicView(
                    topic = content[it],
                    bottomSeparator = bottomSeparator
                )
            }

            item {
                AnimatedVisibility(visible = isLoadingMore) {
                    ProgressLoader()
                }
            }
        }
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

package com.alexmurz.composetexter.effect

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun LazyListLoadMoreEffect(
    lazyListState: LazyListState,
    threshold: Int = 3,
    onLoadMore: suspend () -> Unit
) {
    val currentLoadMore by rememberUpdatedState(onLoadMore)

    val loadMore by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - threshold)
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { loadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                currentLoadMore()
            }
    }
}

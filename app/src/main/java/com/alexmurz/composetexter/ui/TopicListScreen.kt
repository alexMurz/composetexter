package com.alexmurz.composetexter.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val peekHeight = 50.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopicListScreen() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val topicListState = rememberTopicListState()

    BackHandler(enabled = scaffoldState.bottomSheetState.isExpanded) {
        scope.launch {
            scaffoldState.bottomSheetState.collapse()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                TopicList(topicListState)
            }
        },
        sheetContent = {
            TopicCreate(
                headerHeight = peekHeight,
                onHeaderClicker = {
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                },
                onComplete = { topic ->
                    if (topic != null) topicListState.onTopicCreated(topic)
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                }
            )
        }
    )

}

package com.alexmurz.composetexter.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.composetexter.ui.topic.TopicCreate
import com.alexmurz.composetexter.ui.topic.TopicList
import com.alexmurz.composetexter.ui.topic.rememberTopicListState
import com.alexmurz.composetexter.viewmodel.TopicCreateViewModel
import com.alexmurz.composetexter.viewmodel.TopicListViewModel
import com.alexmurz.messages.model.MessageChainParent
import com.alexmurz.topic.service.TopicServiceContext
import kotlinx.coroutines.launch

private val peekHeight = 50.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopicListScreen(
    openMessageChain: (MessageChainParent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val context = remember { TopicServiceContext(25) }

    val topicListVM: TopicListViewModel = viewModel(initializer = {
        TopicListViewModel(context)
    })
    val topicCreateVM = viewModel(initializer = {
        TopicCreateViewModel(context)
    })

    val topicListState = rememberTopicListState(topicListVM)

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
                TopicList(
                    state = topicListState,
                    onTopicClicked = {
                        openMessageChain(it.messageChainParent)
                    }
                )
            }
        },
        sheetContent = {
            TopicCreate(
                viewModel = topicCreateVM,
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

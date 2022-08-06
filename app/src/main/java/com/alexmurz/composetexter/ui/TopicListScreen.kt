package com.alexmurz.composetexter.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class State {
    List, Create;
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TopicListScreen() {
    var state by remember {
        mutableStateOf(State.List)
    }

    val topicListState = rememberTopicListState()

    AnimatedContent(
        targetState = state,
        transitionSpec = {
            when (targetState) {
                State.List -> fadeIn() with slideOut { IntOffset(it.width, 0) }
                State.Create -> slideIn { IntOffset(it.width, 0) } with fadeOut()
            }
        },
    ) {
        when (it) {
            State.List -> ListView(topicListState) { state = State.Create }
            State.Create -> TopicCreate { topic ->
                if (topic != null) topicListState.onTopicCreated(topic)
                state = State.List
            }

//            CreateView { state = State.List }
        }
    }
}

@Composable
private fun ListView(
    topicListState: TopicListState,
    onCreateTopicClicked: () -> Unit
) {
    val currentOnCreateTopicClicked by rememberUpdatedState(onCreateTopicClicked)

    Scaffold(
        scaffoldState = rememberScaffoldState(),
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Button(onClick = currentOnCreateTopicClicked) {
                Text("+")
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            TopicList(topicListState)
        }
    }
}

@Composable
private fun CreateView(
    onCompleted: () -> Unit,
) {
//    val currentOnCompleted by rememberUpdatedState(onCompleted)
//
//    val scope = rememberCoroutineScope()
//
//    var busy by remember {
//        mutableStateOf(false)
//    }
//
//    BackHandler(enabled = true) {
//        if (!busy) currentOnCompleted()
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Text(
//            text = "Create view",
//            modifier = Modifier.fillMaxWidth()
//        )
//        Button(
//            enabled = !busy,
//            onClick = {
//                scope.launch {
//                    busy = true
//                    delay(1_500)
//                    currentOnCompleted()
//                }
//            }
//        ) {
//            Text("Create test")
//        }
//        AnimatedVisibility(
//            visible = busy,
//            enter = fadeIn(),
//            exit = fadeOut(),
//        ) {
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//        }
//    }
}

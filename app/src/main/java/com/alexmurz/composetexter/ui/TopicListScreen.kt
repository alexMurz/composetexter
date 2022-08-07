package com.alexmurz.composetexter.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.alexmurz.topic.model.Topic

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

    Box {
        ListView(topicListState) { state = State.Create }
        CreateView(visible = state == State.Create) { topic ->
            if (topic != null) topicListState.onTopicCreated(topic)
            state = State.List
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
    visible: Boolean,
    onComplete: (Topic?) -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1.0f else 0.0f
    )

    Box(
        modifier = Modifier.background(Color.Black.copy(alpha = alpha * 0.10f)),
    ) {
        val spec = remember {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow,
                visibilityThreshold = IntOffset.VisibilityThreshold
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(spec) { it / 2 },
            exit = slideOutVertically(spec) { it / 2 },
        ) {
            TopicCreate(onComplete = onComplete)
        }
    }
}

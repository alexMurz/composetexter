package com.alexmurz.composetexter.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.composetexter.R
import com.alexmurz.composetexter.viewmodel.TopicCreateViewModel
import com.alexmurz.topic.model.Topic
import kotlinx.coroutines.launch

@Composable
fun TopicCreate(
    viewModel: TopicCreateViewModel = viewModel(),
    onComplete: (Topic?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val currentOnComplete = rememberUpdatedState(onComplete)

    val titleValue by viewModel.titleFieldStateFlow.collectAsState()
    val messageValue by viewModel.messageFieldStateFlow.collectAsState()

    val focus = LocalFocusManager.current

    var busy by remember {
        mutableStateOf(false)
    }

    val createTopicAction: () -> Unit by rememberUpdatedState {
        focus.clearFocus(true)
        scope.launch {
            busy = true
            val topic = viewModel.createTopic()
            if (topic != null) {
                currentOnComplete.value(topic)
            } else {
                busy = false
            }
        }
    }

    val keyboardActions = KeyboardActions(
        onNext = {
            focus.moveFocus(FocusDirection.Next)
        },
        onGo = {
            createTopicAction()
        }
    )

    BackHandler {
        if (!busy) currentOnComplete.value(null)
    }

    val outerBoxModifier = remember {
        Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    focus.clearFocus(true)
                    if (!busy) currentOnComplete.value(null)
                }
            )
    }

    Box(modifier = outerBoxModifier) {
        val surfaceModifier = remember {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {}
                )
        }
        Surface(
            modifier = surfaceModifier,
            elevation = 20.dp,
        ) {
            Column(
                modifier = Modifier.padding(bottom = 20.dp),
            ) {
                AnimatedVisibility(
                    visible = busy,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = stringResource(id = R.string.create_topic),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(
                            top = 10.dp,
                            bottom = 10.dp
                        ),
                )

                Field(
                    value = titleValue.value,
                    placeholder = stringResource(id = R.string.title),
                    busy = busy,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = keyboardActions,
                    onValueChanged = { viewModel.onTitleChanged(it) }
                )
                Field(
                    value = messageValue.value,
                    placeholder = stringResource(id = R.string.message),
                    busy = busy,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go,
                    ),
                    keyboardActions = keyboardActions,
                    onValueChanged = { viewModel.onMessageChanged(it) }
                )

                Button(
                    modifier = Modifier.align(Alignment.End),
                    enabled = !busy,
                    onClick = createTopicAction
                ) {
                    Text(
                        text = stringResource(id = R.string.create),
                    )
                }
            }
        }
    }
}

@Composable
private fun Field(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    busy: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    onValueChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        enabled = !busy,
        placeholder = {
            Text(placeholder)
        },
        onValueChange = onValueChanged,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
    )
}

@Preview
@Composable
private fun Preview() {
    TopicCreate {}
}

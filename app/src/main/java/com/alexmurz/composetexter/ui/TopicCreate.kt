package com.alexmurz.composetexter.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

    val titleValue by viewModel.titleFieldStateFlow.collectAsState()
    val messageValue by viewModel.messageFieldStateFlow.collectAsState()

    var busy by remember {
        mutableStateOf(false)
    }

    BackHandler {
        if (!busy) onComplete(null)
    }

    Column {
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
                .padding(bottom = 5.dp),
        )

        Field(
            value = titleValue.value,
            placeholder = stringResource(id = R.string.title),
            busy = busy,
            onValueChanged = { viewModel.onTitleChanged(it) }
        )
        Field(
            value = messageValue.value,
            placeholder = stringResource(id = R.string.message),
            busy = busy,
            onValueChanged = { viewModel.onMessageChanged(it) }
        )

        Button(
            modifier = Modifier.align(Alignment.End),
            enabled = !busy,
            onClick = {
                scope.launch {
                    busy = true
                    val topic = viewModel.createTopic()
                    if (topic != null) {
                        onComplete(topic)
                    } else {
                        busy = false
                    }
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.create),
            )
        }
    }
}

@Composable
private fun Field(
    value: String,
    placeholder: String,
    busy: Boolean,
    onValueChanged: (String) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        enabled = !busy,
        placeholder = {
            Text(placeholder)
        },
        onValueChange = onValueChanged
    )
}

@Preview
@Composable
private fun Preview() {
    TopicCreate {

    }
}
package com.alexmurz.composetexter.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexmurz.composetexter.apperror.AppErrorWrapper
import com.alexmurz.composetexter.apperror.ERROR_VISIBILITY_DURATION_MILLIS
import com.alexmurz.composetexter.apperror.HumanReadableError
import com.alexmurz.composetexter.viewmodel.AppErrorViewModel
import kotlinx.coroutines.delay

private val EnterAnimation = expandIn(
    initialSize = {
        IntSize(it.width, 0)
    }
)

private val ExitAnimation = shrinkOut(
    targetSize = {
        IntSize(it.width, 0)
    }
)


@Composable
fun AppErrorView(
    viewModel: AppErrorViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colors.error)
        ) {
            val errors by viewModel.errorList
            items(
                items = errors,
                key = AppErrorWrapper::seqId,
                itemContent = { error ->
                    ErrorView(
                        error = error.error,
                        onErrorDismissed = {
                            viewModel.onErrorDismiss(error)
                        }
                    )
                },
            )
        }
    }
}

@Composable
private fun ErrorView(
    error: HumanReadableError,
    onErrorDismissed: () -> Unit,
) {
    var visible by remember {
        mutableStateOf(false)
    }

    var dismiss by remember {
        mutableStateOf(false)
    }

    val currentOnErrorDismissed by rememberUpdatedState(onErrorDismissed)

    // Launch and dismiss animation controller
    LaunchedEffect(dismiss) {
        if (dismiss) {
            visible = false
            delay(300)
            currentOnErrorDismissed()
        } else {
            visible = true
            delay(ERROR_VISIBILITY_DURATION_MILLIS)
            dismiss = true
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = EnterAnimation,
        exit = ExitAnimation
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = {
                        dismiss = true
                    },
                )
                .padding(
                    vertical = 10.dp,
                    horizontal = 20.dp,
                )
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1.0f),
                text = error.humanMessage,
                color = MaterialTheme.colors.onError,
            )
            CircularCountdownIndicator(
                modifier = Modifier.align(Alignment.CenterVertically),
                duration = ERROR_VISIBILITY_DURATION_MILLIS,
            )
        }
    }
}

@Suppress("SameParameterValue")
@Composable
private fun CircularCountdownIndicator(
    duration: Long,
    modifier: Modifier = Modifier,
): Unit = Box(modifier) {
    var progressTarget by remember {
        mutableStateOf(1.0f)
    }

    val animationSpec = remember(duration) {
        tween<Float>(
            durationMillis = duration.toInt(),
            easing = LinearEasing,
        )
    }

    // Progress starter
    LaunchedEffect(Unit) {
        progressTarget = 0.0f
    }

    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = animationSpec,
    )

    Text(
        modifier = Modifier.align(Alignment.Center),
        text = String.format("%.1f", (progress * duration) / 1000.0f),
        color = MaterialTheme.colors.onError,
    )
    CircularProgressIndicator(
        progress = progress,
        color = MaterialTheme.colors.onError,
    )
}

@Preview
@Composable
private fun CountdownPreview() {
    CircularCountdownIndicator(5000L)
}

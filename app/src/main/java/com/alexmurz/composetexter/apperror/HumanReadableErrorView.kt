package com.alexmurz.composetexter.apperror

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

private val ErrorNotificationEnter = expandIn(
    expandFrom = Alignment.TopCenter,
    initialSize = {
        IntSize(it.width, 0)
    },
)

private val ErrorNotificationExit = shrinkOut(
    shrinkTowards = Alignment.TopCenter,
    targetSize = {
        IntSize(it.width, 0)
    }
)

object HumanReadableErrorView {
    @JvmStatic
    @Composable
    fun DropDown(
        errorFlow: Flow<HumanReadableError>,
        visibilityDurationMillis: Long = 5_000,
    ) {
        val error by errorFlow.collectAsState(null)

        var visible by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(error) {
            if (error == null) return@LaunchedEffect

            visible = true
            delay(visibilityDurationMillis)
            visible = false
        }

        AnimatedVisibility(
            visible = visible,
            enter = ErrorNotificationEnter,
            exit = ErrorNotificationExit,
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .background(Color.Red)
                    .fillMaxWidth()
            ) {
                Text("Error happened, ${error?.humanMessage}")
                Text("Error code: ${error?.humanErrorCode}")
            }
        }
    }

}
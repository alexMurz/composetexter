package com.alexmurz.composetexter.ui.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.libcore.ext.timeStringSince
import com.alexmurz.composetexter.ui.common.LayoutDependentColumn
import com.alexmurz.topic.model.Topic

private val PADDING = DpRect(
    left = 10.dp,
    top = 10.dp,
    right = 10.dp,
    bottom = 10.dp,
)

private val SEPARATOR_HEIGHT = 0.5.dp

@Composable
internal fun TopicView(
    topic: Topic,
    bottomSeparator: Boolean = false,
    now: CATime = CATime.now(),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = PADDING.top,
                start = PADDING.left,
            )
    ) {
        LayoutDependentColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    end = PADDING.right,
                    bottom = PADDING.bottom,
                ),
            primaryContent = {
                Text(
                    text = topic.title,
                    color = TopicViewTheme.colors.title,
                    style = TopicViewTheme.typography.title,
                )
                Text(
                    text = topic.message,
                    color = TopicViewTheme.colors.message,
                    style = TopicViewTheme.typography.message,
                    onTextLayout = ::onTextMeasured,
                )
            },
            dependentContent = {
                val timeString = topic.date.timeStringSince(
                    now = now,
                )
                Text(
                    text = timeString,
                    color = TopicViewTheme.colors.time,
                    style = TopicViewTheme.typography.time,
                )
            }
        )
        if (bottomSeparator) Box(
            modifier = Modifier
                .padding(top = PADDING.bottom)
                .fillMaxWidth()
                .height(SEPARATOR_HEIGHT)
                .background(TopicViewTheme.colors.separator)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val topic = Topic(
        id = 0L,
        date = CATime.of(1_000_000),
        title = "Title",
        message = "Message",
        attachments = emptyList()
    )

    TopicViewTheme {
        TopicView(topic)
    }
}

@Preview
@Composable
private fun PreviewSmall() {
    val topic = Topic(
        id = 0L,
        date = CATime.of(1_000_000),
        title = "1",
        message = "1",
        attachments = emptyList()
    )
    TopicViewTheme {
        TopicView(topic)
    }
}


@Preview("With long single line")
@Composable
private fun PreviewWithLongSingleLine() {
    val topic = Topic(
        id = 0L,
        date = CATime.of(1_000_000),
        title = "Title",
        message = "Message Message Message Message",
        attachments = emptyList()
    )
    TopicViewTheme {
        TopicView(topic)
    }
}

@Preview("With overflow")
@Composable
private fun PreviewWithOverflow() {
    val topic = Topic(
        id = 0L,
        date = CATime.of(1_000_000),
        title = "Title",
        message = "Message Message Message Message Message Message",
        attachments = emptyList()
    )
    TopicViewTheme {
        TopicView(topic)
    }
}

@Preview("With separator")
@Composable
private fun PreviewWithSeparator() {
    val topic = Topic(
        id = 0L,
        date = CATime.of(1_000_000),
        title = "Title",
        message = "Message",
        attachments = emptyList()
    )
    TopicViewTheme {
        TopicView(topic, bottomSeparator = true)
    }
}

@Preview("AsList")
@Composable
private fun PreviewAsList() {
    fun t(time: Long) = Topic(
        id = 0L,
        date = CATime.of(time),
        title = "Title",
        message = "Message",
        attachments = emptyList()
    )

    val now = CATime.now()
    val step = CATime.DAY_MILLIS * 30 // 100 * 1_000_000L

    TopicViewTheme {
        LazyColumn {
            items(99) { i ->
                TopicView(
                    t(now.timestamp - (i * step)),
                    bottomSeparator = i != 99,
                    now = now,
                )
            }
        }
    }
}

package com.alexmurz.composetexter.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.libcore.ext.timeStringSince
import com.alexmurz.composetexter.ui.common.LayoutDependentColumn
import com.alexmurz.composetexter.viewmodel.MessageViewModel
import com.alexmurz.messages.model.Message

private val padding = 6.dp
private val corner = 8.dp

private fun shape(
    topStart: Dp = corner,
    topEnd: Dp = corner,
    bottomEnd: Dp = corner,
    bottomStart: Dp = corner
) = RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart)

private val shapes = object {
    val withoutTail = shape()
    val left = shape(bottomStart = 0.dp)
    val right = shape(bottomEnd = 0.dp)
}

@Composable
fun MessageView(
    vm: MessageViewModel,
    modifier: Modifier = Modifier,
) {
    val message = vm.message
    val shape = when {
        vm.isLeft -> when {
            vm.isEndOfChain -> shapes.left
            else -> shapes.withoutTail
        }
        else -> when {
            vm.isEndOfChain -> shapes.right
            else -> shapes.withoutTail
        }
    }

    Box(
        modifier = modifier
            .background(Color.LightGray, shape)
            .padding(all = padding)
    ) {
        LayoutDependentColumn(
            primaryContent = {
                TextView(message.message, ::onTextMeasured)
            },
            dependentContent = {
                DateTimeView(message.dateUpdated)
            }
        )
    }
}

@Composable
private fun TextView(
    text: String,
    onTextMeasured: (TextLayoutResult) -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        onTextLayout = onTextMeasured,
    )
}

@Composable
private fun DateTimeView(time: CATime) {
    Text(
        text = time.timeStringSince(),
        style = MaterialTheme.typography.body2,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Preview
@Composable
private fun Preview1() {
    fun message(text: String) = Message(
        id = 0,
        message = text,
        dateCreated = CATime.now(),
        dateUpdated = CATime.now(),
    )

    Column(
        modifier = Modifier.width(200.dp),
    ) {
        MessageView(
            vm = MessageViewModel(
                message = message("Left Start"),
                isLeft = true,
                isEndOfChain = false,
            )
        )
        MessageView(
            vm = MessageViewModel(
                message = message("Left End"),
                isLeft = true,
                isEndOfChain = true,
            )
        )

        MessageView(
            vm = MessageViewModel(
                message = message("Right Start"),
                isLeft = false,
                isEndOfChain = false,
            )
        )
        MessageView(
            vm = MessageViewModel(
                message = message("Right End"),
                isLeft = false,
                isEndOfChain = true,
            )
        )
    }
}

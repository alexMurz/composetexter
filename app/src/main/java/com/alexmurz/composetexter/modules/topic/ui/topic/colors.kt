package com.alexmurz.composetexter.modules.topic.ui.topic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class TopicViewColors(
    title: Color,
    message: Color,
    time: Color,
    separator: Color,
) {
    var title by mutableStateOf(title)
        internal set

    var message by mutableStateOf(message)
        internal set

    var time by mutableStateOf(time)
        internal set

    var separator by mutableStateOf(separator)
        internal set

    fun copy(
        title: Color = this.title,
        message: Color = this.message,
        time: Color = this.time,
        separator: Color = this.separator,
    ) = TopicViewColors(
        title = title,
        message = message,
        time = time,
        separator = separator
    )

    internal fun updateFrom(colors: TopicViewColors) {
        title = colors.title
        message = colors.message
        time = colors.time
        separator = colors.separator
    }

    companion object {
        fun lightColors() = TopicViewColors(
            title = Color.Black,
            message = Color.Black,
            time = Color.Black,
            separator = Color.Black,
        )

        fun darkColors() = TopicViewColors(
            title = Color.White,
            message = Color.White,
            time = Color.White,
            separator = Color.White,
        )
    }
}



internal val LocalTopicViewColors = staticCompositionLocalOf {
    TopicViewColors.lightColors()
}

package com.alexmurz.composetexter.ui.topic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember

@Composable
fun TopicViewTheme(
    colors: TopicViewColors = TopicViewTheme.colors,
    typography: TopicViewTypography = TopicViewTheme.typography,
    content: @Composable () -> Unit,
) {
    val rColors = remember { colors.copy() }.apply {
        updateFrom(colors)
    }

    CompositionLocalProvider(
        LocalTopicViewColors provides rColors,
        LocalTopicViewTypography provides typography,
    ) {
        content()
    }
}

object TopicViewTheme {
    val colors: TopicViewColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTopicViewColors.current

    val typography: TopicViewTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTopicViewTypography.current
}

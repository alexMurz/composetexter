package com.alexmurz.composetexter.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.alexmurz.composetexter.libcore.util.Pool
import com.alexmurz.composetexter.libcore.util.withValue
import kotlin.concurrent.getOrSet
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

private const val PRIMARY_CONTENT = 0
private const val DEPENDENT_CONTENT = 1

interface LayoutDependentColumnScope {
    fun onTextMeasured(result: TextLayoutResult)
}

private class LayoutScopeImpl : LayoutDependentColumnScope {
    var result: TextLayoutResult? = null

    override fun onTextMeasured(result: TextLayoutResult) {
        this.result = result
    }
}

private val threadLocalScopes = ThreadLocal<Pool<LayoutScopeImpl>>()

@OptIn(ExperimentalContracts::class)
private inline fun <R> withScope(action: (LayoutScopeImpl) -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    val scope = threadLocalScopes.getOrSet {
        Pool { LayoutScopeImpl() }
    }
    return scope.withValue(action)
}

/**
 * Place primary composables at with left alignment and dependent with right
 * dependent composables are moved up if space is available
 *
 * Dependent content moved up when last item in primary and first in secondary total width
 * less that max
 *
 * Designed to primarily be used with single text in primary and secondary slots
 *
 * Then primary content and with Text use onTextMeasured method of scope
 */
@Composable
fun LayoutDependentColumn(
    modifier: Modifier = Modifier,
    primaryContent: @Composable LayoutDependentColumnScope.() -> Unit,
    dependentContent: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->

        val primaryPlaceables: List<Placeable>
        val dependentPlaceables: List<Placeable>
        val intersection: Int

        withScope { scope ->
            primaryPlaceables = subcompose(PRIMARY_CONTENT) {
                primaryContent(scope)
            }.map {
                it.measure(constraints)
            }

            dependentPlaceables = subcompose(DEPENDENT_CONTENT) {
                dependentContent()
            }.map {
                it.measure(constraints)
            }

            intersection = findContentIntersection(
                scope,
                constraints.maxWidth,
                primaryPlaceables.lastOrNull(),
                dependentPlaceables.firstOrNull()
            )
        }

        val primarySize = primaryPlaceables.columnSize()
        val dependentSize = dependentPlaceables.columnSize()


        val totalWidth = primarySize.width + dependentSize.width
        val width = if (totalWidth < constraints.maxWidth) totalWidth
        else max(primarySize.width, dependentSize.width)

        layout(
            width = width,
            height = primarySize.height + dependentSize.height - intersection
        ) {
            var yPos = 0
            primaryPlaceables.forEach {
                it.placeRelative(0, yPos)
                yPos += it.height
            }

            yPos -= intersection
            dependentPlaceables.forEach {
                it.placeRelative(width - it.width, yPos)
                yPos += it.height
            }
        }
    }
}

private fun List<Placeable>.columnSize() =
    fold(IntSize.Zero) { currentMax: IntSize, placeable: Placeable ->
        IntSize(
            width = max(currentMax.width, placeable.width),
            height = currentMax.height + placeable.height
        )
    }

private fun findContentIntersection(
    scope: LayoutScopeImpl,
    maxWidth: Int,
    primary: Placeable?,
    secondary: Placeable?,
): Int {
    if (primary == null || secondary == null) return 0
    val layoutResult = scope.result

    val lastLineTop: Int
    val lastLineBottom: Int
    val lastLineWidth: Int
    if (layoutResult != null) with(layoutResult) {
        val idx = lineCount - 1
        lastLineTop = getLineTop(idx).toInt()
        lastLineBottom = getLineBottom(idx).toInt()
        lastLineWidth = (getLineRight(idx) - getLineLeft(idx)).toInt().absoluteValue
    } else {
        lastLineTop = 0
        lastLineBottom = primary.height
        lastLineWidth = primary.width
    }

    return if (secondary.width + lastLineWidth >= maxWidth) 0
    else {
        val lastLineHeight = lastLineBottom - lastLineTop
        min(lastLineHeight, secondary.height)
    }
}


@Preview
@Composable
private fun Preview() {
    Box(
        modifier = Modifier
            .width(200.dp)
    ) {
        LayoutDependentColumn(
            primaryContent = {
                Text(
                    "Title",
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    "Message 123 123 123 123 123 123 123 123 123 123 123 123 123 123 123 123",
                    style = MaterialTheme.typography.body2,
                    onTextLayout = ::onTextMeasured
                )
            },
            dependentContent = {
                Text(
                    "Subcontent 1",
                    style = MaterialTheme.typography.caption,
                )
            }
        )
    }
}

@Preview
@Composable
private fun Preview2() {
    Box(
        modifier = Modifier
            .width(200.dp)
    ) {
        LayoutDependentColumn(
            primaryContent = {
                Text(
                    "Title",
                    style = MaterialTheme.typography.body1,
                )
                Text(
                    "Message 123 123 123 123 123 123 123 123 123 123 123 123",
                    style = MaterialTheme.typography.body2,
                    onTextLayout = ::onTextMeasured
                )
            },
            dependentContent = {
                Text(
                    "Subcontent 1",
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    "Subcontent 2",
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    "Subcontent 3",
                    style = MaterialTheme.typography.caption,
                )
            }
        )
    }
}

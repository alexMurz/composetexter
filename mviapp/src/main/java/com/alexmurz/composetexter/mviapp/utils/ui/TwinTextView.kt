package com.alexmurz.composetexter.mviapp.utils.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import androidx.core.graphics.withTranslation
import kotlin.math.max
import kotlin.math.min

private fun staticLayout(
    paint: TextPaint,
    content: String,
    width: Int,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    spacingMult: Float = 1.0f,
    spacingAdd: Float = 0.0f,
    includePad: Boolean = false,
    ellipsize: TextUtils.TruncateAt = TextUtils.TruncateAt.END,
    ellipsizedWidth: Int = width,
): StaticLayout {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder
            .obtain(
                content, 0, content.length, paint, width
            )
            .setAlignment(alignment)
            .setLineSpacing(spacingAdd, spacingMult)
            .setIncludePad(includePad)
            .setEllipsize(ellipsize)
            .setEllipsizedWidth(ellipsizedWidth)
            .build()
    } else {
        StaticLayout(
            content,
            0,
            content.length,
            paint,
            width,
            alignment,
            spacingMult,
            spacingAdd,
            includePad,
            ellipsize,
            ellipsizedWidth
        )
    }
}

private val Layout.maxLineWidth: Int
    inline get() = (0 until lineCount).fold(0f) { acc, idx ->
        max(acc, getLineWidth(idx))
    }.toInt()

private val Layout.lastLineWidth: Float
    inline get() = getLineWidth(lineCount - 1)

private val Layout.firstLineWidth: Float
    inline get() = getLineWidth(0)

private fun Layout.getLineHeight(line: Int): Int = getLineAscent(line)

// returns Pair<X, Y> position to start `b` layout when `a` is at (0, 0)
private fun intersect(
    a: Layout,
    b: Layout,
    maxWidth: Int,
    minWidth: Int,
    margin: Int = 0
): Pair<Int, Int> {
    val last = a.lastLineWidth
    val first = b.firstLineWidth
    return if (last + first + margin > maxWidth) 0 to 0
    else {
        val h1 = a.getLineHeight(a.lineCount - 1)
        val h2 = b.getLineHeight(0)
        val bWidth = b.maxLineWidth
        val positionForRightAlign = minWidth - bWidth
        val positionToFollowALastLine = last.toInt() + margin

        max(positionForRightAlign, positionToFollowALastLine) to min(h1, h2)
    }
}

class TwinTextView(
    context: Context
) : View(context) {
    var leftContent: String = ""
    var rightContent: String = ""

    private var leftLayout: Layout? = null
    private var rightLayout: Layout? = null
    private var rightXOffset: Float = 0f
    private var rightYOffset: Float = 0f


    private val leftPaint = TextPaint().apply {
        textSize = spToPx(16f)
    }
    private val rightPaint = TextPaint().apply {
        textSize = spToPx(12f)
        color = Color.DKGRAY
    }


    var leftTextSize by leftPaint::textSize
    var leftTextColor by leftPaint::color

    var rightTextSize by rightPaint::textSize
    var rightTextColor by rightPaint::color


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val maxTextWidth = MeasureSpec.getSize(widthMeasureSpec)
        val minTextWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            maxTextWidth
        } else {
            0
        }

        val leftLayout = staticLayout(
            paint = leftPaint,
            content = leftContent.trim(),
            width = maxTextWidth,
        ).also {
            this.leftLayout = it
        }

        val rightLayout = staticLayout(
            paint = rightPaint,
            content = rightContent.trim(),
            width = maxTextWidth
        ).also {
            this.rightLayout = it
        }

        val rightWidth = rightLayout.maxLineWidth
        val (xStart, yStart) = intersect(
            a = leftLayout,
            b = rightLayout,
            maxWidth = maxTextWidth,
            minWidth = minTextWidth,
            margin = dpToPxi(4)
        )

        // Left width might be > maxTextWidth when its trailed by spaces
        val measuredWidth = run {
            val subtotal = xStart + rightWidth
            min(subtotal, maxTextWidth).coerceIn(minTextWidth, maxTextWidth)
        }
        rightXOffset = (measuredWidth - rightWidth).toFloat()
        rightYOffset = (leftLayout.height + yStart).toFloat()
        val measuredHeight = max(rightYOffset.toInt() + rightLayout.height, leftLayout.height)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        leftLayout?.draw(canvas)
        rightLayout?.let { rightLayout ->
            canvas.withTranslation(rightXOffset, rightYOffset) {
                rightLayout.draw(canvas)
            }
        }
    }
}

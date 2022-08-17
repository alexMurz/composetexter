package com.alexmurz.composetexter.mviapp.components.topiclist.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintProperties
import com.alexmurz.composetexter.mviapp.utils.add
import com.alexmurz.composetexter.mviapp.utils.dpToPx
import com.google.android.material.color.MaterialColors

private const val PADDING_LEFT = 8
private const val PADDING_TOP = 8
private const val PADDING_RIGHT = 8
private const val PADDING_BOTTOM = 8

class TopicView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    init {
        val color = MaterialColors.getColor(context, android.R.attr.colorPrimary, Color.BLACK)
        val rippleStateList = ColorStateList.valueOf(color)
        val backgroundColor = ColorDrawable(Color.WHITE)

        background = RippleDrawable(
            rippleStateList,
            backgroundColor,
            null,
        )

        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        setPadding(
            dpToPx(PADDING_LEFT),
            dpToPx(PADDING_TOP),
            dpToPx(PADDING_RIGHT),
            dpToPx(PADDING_BOTTOM),
        )
    }

    val title = add(AppCompatTextView(context)) {
        id = TITLE
        layoutParams = LayoutParams(
            ConstraintProperties.WRAP_CONTENT,
            ConstraintProperties.WRAP_CONTENT,
        ).apply {
            leftToLeft = PARENT
            topToTop = PARENT
        }
    }

    val message = add(AppCompatTextView(context)) {
        id = MESSAGE
        layoutParams = LayoutParams(
            ConstraintProperties.WRAP_CONTENT,
            ConstraintProperties.WRAP_CONTENT,
        ).apply {
            leftToLeft = PARENT
            topToBottom = TITLE
        }
    }

    val time = add(AppCompatTextView(context)) {
        id = TIME
        layoutParams = LayoutParams(
            ConstraintProperties.WRAP_CONTENT,
            ConstraintProperties.WRAP_CONTENT,
        ).apply {
            rightToRight = PARENT
            topToBottom = MESSAGE
        }
    }

    companion object {
        private const val PARENT = LayoutParams.PARENT_ID
        private const val TITLE = 1
        private const val MESSAGE = 2
        private const val TIME = 3
    }
}

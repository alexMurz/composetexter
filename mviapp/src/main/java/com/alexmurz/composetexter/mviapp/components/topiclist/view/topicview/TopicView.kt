package com.alexmurz.composetexter.mviapp.components.topiclist.view.topicview

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
import com.alexmurz.composetexter.mviapp.utils.ui.TwinTextView
import com.alexmurz.composetexter.mviapp.utils.ui.add
import com.alexmurz.composetexter.mviapp.utils.ui.dpToPxi
import com.alexmurz.composetexter.mviapp.utils.ui.matchParent
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
            dpToPxi(PADDING_LEFT),
            dpToPxi(PADDING_TOP),
            dpToPxi(PADDING_RIGHT),
            dpToPxi(PADDING_BOTTOM),
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

    val messageContainer = add(ConstraintLayout(context)) {
        layoutParams = LayoutParams(
            ConstraintProperties.MATCH_CONSTRAINT,
            ConstraintProperties.WRAP_CONTENT,
        ).apply {
            leftToLeft = PARENT
            topToBottom = TITLE
            rightToRight = PARENT
        }

//        val pad = dpToPx(8)
//        val corner = dpToPx(14)
//        setPadding(pad, pad, pad, pad)
//
//        background = GradientDrawable().apply {
//            setColor(Color.LTGRAY)
//            cornerRadius = corner.toFloat()
//        }
    }

    val messageTimeText = messageContainer.add(TwinTextView(context)) {
        matchParent()
    }

    companion object {
        private const val PARENT = LayoutParams.PARENT_ID
        private const val TITLE = 1
    }
}

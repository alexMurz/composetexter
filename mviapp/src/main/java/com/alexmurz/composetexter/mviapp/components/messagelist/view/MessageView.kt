package com.alexmurz.composetexter.mviapp.components.messagelist.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
import com.alexmurz.composetexter.mviapp.utils.ui.TwinTextView
import com.alexmurz.composetexter.mviapp.utils.ui.add
import com.alexmurz.composetexter.mviapp.utils.ui.dpToPxi
import com.alexmurz.composetexter.mviapp.utils.ui.matchParent

class MessageView(
    context: Context
): ConstraintLayout(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    private val messageContainer = add(ConstraintLayout(context)) {
        id = MESSAGE_CONTAINER
        layoutParams = LayoutParams(0, WRAP_CONTENT).apply {
            matchConstraintMaxWidth = WRAP_CONTENT
            matchConstraintPercentWidth = 0.65f
        }

        val pad = dpToPxi(8)
        setPadding(pad, pad, pad, pad)
        val corner = dpToPxi(14)
        background = GradientDrawable().apply {
            setColor(Color.LTGRAY)
            cornerRadius = corner.toFloat()
        }
    }

    val message = messageContainer.add(TwinTextView(context)) {
        matchParent()
    }

    companion object {
        private const val MESSAGE_CONTAINER = 1
    }
}

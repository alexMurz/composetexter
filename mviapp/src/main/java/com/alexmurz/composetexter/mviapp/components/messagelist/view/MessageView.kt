package com.alexmurz.composetexter.mviapp.components.messagelist.view

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.alexmurz.composetexter.mviapp.utils.add
import com.alexmurz.composetexter.mviapp.utils.dpToPx

class MessageView(
    context: Context
): ConstraintLayout(context) {

    init {
        val padding = dpToPx(8)
        setPadding(padding, padding, padding, padding)
    }

    val message = add(AppCompatTextView(context)) {
    }

}
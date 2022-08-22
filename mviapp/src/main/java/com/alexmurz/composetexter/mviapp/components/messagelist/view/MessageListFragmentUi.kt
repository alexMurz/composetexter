package com.alexmurz.composetexter.mviapp.components.messagelist.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexmurz.composetexter.mviapp.utils.ui.VerticalSpaceItemDecoration
import com.alexmurz.composetexter.mviapp.utils.ui.add
import com.alexmurz.composetexter.mviapp.utils.ui.dpToPxi
import com.alexmurz.composetexter.mviapp.utils.ui.matchParent


class MessageListFragmentUi(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        background = ColorDrawable(Color.WHITE)

        setOnClickListener { }
    }

    val recycler = add(RecyclerView(context)) {
        matchParent()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)

        addItemDecoration(
            VerticalSpaceItemDecoration(
                bottomSpacing = dpToPxi(4),
                clipBottomEnd = true,
            )
        )
    }
}

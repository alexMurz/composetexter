package com.alexmurz.composetexter.mviapp.components.topiclist.view

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alexmurz.composetexter.mviapp.utils.add
import com.alexmurz.composetexter.mviapp.utils.matchParent


internal class TopicListUi(context: Context) : ConstraintLayout(context) {

    init {
        matchParent()
    }

    val refreshLayout = add(SwipeRefreshLayout(context)) {
        matchParent()
    }

    val recyclerView = refreshLayout.add(RecyclerView(context)) {
        matchParent()
    }
}

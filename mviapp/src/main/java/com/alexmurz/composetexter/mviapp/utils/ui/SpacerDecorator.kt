package com.alexmurz.composetexter.mviapp.utils.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

private val Boolean.int
    inline get() = if (this) 1 else 0

private fun Rect.mul(other: Rect) {
    left *= other.left
    top *= other.top
    right *= other.right
    bottom *= other.bottom
}

class VerticalSpaceItemDecoration(
    private val leftSpacing: Int = 0,
    clipLeftStart: Boolean = false,
    clipLeftEnd: Boolean = false,
    private val topSpacing: Int = 0,
    clipTopStart: Boolean = false,
    clipTopEnd: Boolean = false,
    private val rightSpacing: Int = 0,
    clipRightStart: Boolean = false,
    clipRightEnd: Boolean = false,
    private val bottomSpacing: Int = 0,
    clipBottomStart: Boolean = false,
    clipBottomEnd: Boolean = false,
) : RecyclerView.ItemDecoration() {

    private val startClipping = Rect(
        clipLeftStart.int,
        clipTopStart.int,
        clipRightStart.int,
        clipBottomStart.int
    )

    private val endClipping = Rect(
        clipLeftEnd.int,
        clipTopEnd.int,
        clipRightEnd.int,
        clipBottomEnd.int
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        outRect.apply {
            set(leftSpacing, topSpacing, rightSpacing, bottomSpacing)
            when (pos) {
                0 -> mul(startClipping)
                itemCount - 1 -> mul(endClipping)
            }
        }
    }

}
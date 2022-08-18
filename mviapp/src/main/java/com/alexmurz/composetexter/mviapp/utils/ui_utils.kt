@file:Suppress("NOTHING_TO_INLINE")

package com.alexmurz.composetexter.mviapp.utils

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.recyclerview.scrollEvents
import io.reactivex.rxjava3.core.Observable

inline fun View.matchParent() {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
    )
}

inline fun <T : View> ViewGroup.add(view: T, apply: T.() -> Unit): T {
    addView(view)
    apply(view)
    return view
}

fun View.dpToPx(dp: Int): Int {
    val d = context.resources.displayMetrics.density
    return (dp * d).toInt()
}

fun View.spToPx(sp: Int): Int {
    val d = context.resources.displayMetrics.scaledDensity
    return (sp * d).toInt()
}

/**
 * Scroll to end events
 *
 * Only supported for LinearLayoutManager
 */
fun RecyclerView.loadMores(threshold: Int): Observable<*> {
    val layoutManager = layoutManager as LinearLayoutManager
    return scrollEvents()
        .map {
            adapter?.itemCount?.let { itemCount ->
                val remaining = itemCount - layoutManager.findLastVisibleItemPosition()
                remaining < threshold
            } ?: false
        }
        .distinctUntilChanged()
        .filter { it }
}

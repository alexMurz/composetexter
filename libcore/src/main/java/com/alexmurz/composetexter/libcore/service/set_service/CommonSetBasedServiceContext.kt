package com.alexmurz.composetexter.libcore.service.set_service

import com.alexmurz.composetexter.libcore.util.getAndUpdateValue
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KMutableProperty0

// Loading boundary for service context
data class Boundary(
    // True if upper boundary reached
    val upper: Boolean = false,
    // True if lower boundary reached
    val lower: Boolean = false,
)

@Suppress("NOTHING_TO_INLINE")
inline fun KMutableProperty0<Boundary>.update(
    upper: Boolean? = null,
    lower: Boolean? = null,
) {
    val v = get()
    set(
        v.copy(
            upper = upper ?: v.upper,
            lower = lower ?: v.lower,
        )
    )
}

abstract class CommonSetBasedServiceContext<T>(
    /**
     * Max amount of items to load at one time
     */
    val limit: Int,
) {
    private val dataSet = AtomicReference(emptySet<T>())

    val data: Set<T>
        get() = dataSet.get()

    var localBoundary: Boundary = Boundary()
        internal set

    var remoteBoundary: Boundary = Boundary()
        internal set

    val initialized: Boolean
        // localBoundary.upper sets to true during initialization
        get() = localBoundary.upper

    val upToDate: Boolean
        get() = localBoundary.upper && remoteBoundary.upper

    val hasContentFromStart: Boolean
        get() = localBoundary.lower && remoteBoundary.lower

    abstract fun getNewerReference(): T?

    abstract fun getOlderReference(): T?

    open fun addItems(items: Iterable<T>) {
        dataSet.getAndUpdateValue {
            it!! + items
        }
    }
}

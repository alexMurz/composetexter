/**
 * Implementation of generic SetBased services via extensions
 */

package com.alexmurz.data.use_case

import com.alexmurz.composetexter.libcore.service.set_service.AbstractSetBasedContext
import com.alexmurz.composetexter.libcore.service.set_service.update

// handle types
typealias LoadNewestHandle<T> = suspend (AbstractSetBasedContext<T>) -> Set<T>
typealias LoadNewerHandle<T> = suspend (AbstractSetBasedContext<T>, T) -> Set<T>
typealias LoadOlderHandle<T> = suspend (AbstractSetBasedContext<T>, T) -> Set<T>
typealias SaveHandle<T> = suspend (AbstractSetBasedContext<T>, Set<T>) -> Unit

/**
 * Initialize from local source
 * Return null if already initialized or no data available
 */
suspend fun <T : Any> AbstractSetBasedContext<T>.requireInitialization(
    localLoadNewest: LoadNewestHandle<T>
): Set<T>? {

    if (initialized) return null

    return localLoadNewest(this).also {
        ::localBoundary.update(upper = true, lower = it.isEmpty())
        addItems(it)
    }.takeIf { it.isNotEmpty() }
}

/**
 * Load newest data from remote
 */
suspend fun <T : Any> AbstractSetBasedContext<T>.initializeFromRemote(
    remoteLoadNewest: LoadNewestHandle<T>,
    localSave: SaveHandle<T>,
): Set<T> {
    val loadedData = remoteLoadNewest(this)
    if (loadedData.isNotEmpty()) localSave(this, loadedData)
    ::remoteBoundary.update(upper = true, lower = loadedData.isEmpty())
    return loadedData
}

/**
 * Initialize context from locally available data
 */
suspend fun <T : Any> AbstractSetBasedContext<T>.initialize(
    localLoadNewest: LoadNewestHandle<T>
): Set<T> {
    return requireInitialization(localLoadNewest) ?: emptySet()
}

/**
 * Update local data from remote source
 */
suspend fun <T : Any> AbstractSetBasedContext<T>.loadNewer(
    localLoadNewest: LoadNewestHandle<T>,
    remoteLoadNewest: LoadNewestHandle<T>,
    remoteLoadNewer: LoadNewerHandle<T>,
    localSave: SaveHandle<T>,
): Set<T> {
    // requireInitialization insures that context.localBoundary.upper == true
    requireInitialization(localLoadNewest)?.let { return it }

    val reference = getNewerReference()
    val loadedData = mutableSetOf<T>()

    // No items locally available, load newest from remote
    if (reference == null) {
        loadedData += initializeFromRemote(remoteLoadNewest, localSave)
    } else {
        // remote upper boundary is not reached
        loadedData += remoteLoadNewer(this, reference)
        if (loadedData.isNotEmpty()) localSave(this, loadedData)
        ::remoteBoundary.update(upper = loadedData.isEmpty())
    }

    addItems(loadedData)
    return loadedData
}

/**
 * Load more (older) data from local/remote source
 */
suspend fun <T : Any> AbstractSetBasedContext<T>.loadOlder(
    localLoadNewest: LoadNewestHandle<T>,
    localLoadOlder: LoadOlderHandle<T>,
    remoteLoadNewest: LoadNewestHandle<T>,
    remoteLoadOlder: LoadOlderHandle<T>,
    localSave: SaveHandle<T>,
): Set<T> {
    requireInitialization(localLoadNewest)?.let { return it }

    if (hasContentFromStart) return emptySet()

    val reference: T = getOlderReference() ?: run {
        return initializeFromRemote(remoteLoadNewest, localSave)
    }

    val loadedData = mutableSetOf<T>()

    if (!localBoundary.lower) {
        loadedData += localLoadOlder(this, reference)
        ::localBoundary.update(lower = loadedData.isEmpty())
    }

    if (!remoteBoundary.lower && loadedData.isEmpty()) {
        loadedData += remoteLoadOlder(this, reference)
        if (loadedData.isNotEmpty()) localSave(this, loadedData)
        ::remoteBoundary.update(lower = loadedData.isEmpty())
    }

    addItems(loadedData)
    return loadedData
}

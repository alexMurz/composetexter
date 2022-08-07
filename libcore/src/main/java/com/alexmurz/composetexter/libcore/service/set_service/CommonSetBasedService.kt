package com.alexmurz.composetexter.libcore.service.set_service

/**
 * Base set-based service implementation with `local` and `remote` sources
 */
abstract class CommonSetBasedService<T, CTX>
    where CTX : CommonSetBasedServiceContext<T>
{
    protected abstract suspend fun localLoadNewest(context: CTX): Set<T>
    protected abstract suspend fun localLoadOlder(context: CTX, reference: T): Set<T>
    protected abstract suspend fun localSave(context: CTX, data: Set<T>)

    protected abstract suspend fun remoteLoadNewest(context: CTX): Set<T>
    protected abstract suspend fun remoteLoadNewer(context: CTX, reference: T): Set<T>
    protected abstract suspend fun remoteLoadOlder(context: CTX, reference: T): Set<T>

    /**
     * Initialize from local source
     * Return null if already initialized or no data available
     */
    protected suspend fun requireInitialization(context: CTX): Set<T>? {
        if (context.initialized) return null

        return localLoadNewest(context).also {
            context::localBoundary.update(upper = true, lower = it.isEmpty())
            context.addItems(it)
        }.takeIf { it.isNotEmpty() }
    }

    /**
     * Load newest data from remote
     */
    protected suspend fun initializeFromRemote(context: CTX): Set<T> {
        val loadedData = remoteLoadNewest(context)
        localSave(context, loadedData)
        context::remoteBoundary.update(upper = true, lower = loadedData.isEmpty())
        return loadedData
    }

    /**
     * Initialize context from locally available data
     */
    suspend fun initialize(context: CTX): Set<T> {
        return requireInitialization(context) ?: emptySet()
    }

    /**
     * Update local data from remote source
     */
    suspend fun loadNewer(context: CTX): Set<T> {
        // requireInitialization insures that context.localBoundary.upper == true
        requireInitialization(context)?.let { return it }

        if (context.upToDate) return emptySet()

        val reference = context.getNewerReference()
        val loadedData = mutableSetOf<T>()

        when {
            // No items locally available, load newest from remote
            reference == null -> {
                loadedData += initializeFromRemote(context)
            }
            // remote upper boundary is not reached
            !context.remoteBoundary.upper -> {
                loadedData += remoteLoadNewer(context, reference)
                localSave(context, loadedData)
                context::remoteBoundary.update(upper = loadedData.isEmpty())
            }
        }

        context.addItems(loadedData)
        return loadedData
    }

    /**
     * Load more (older) data from local/remote source
     */
    suspend fun loadOlder(context: CTX): Set<T> {
        requireInitialization(context)?.let { return it }

        if (context.hasContentFromStart) return emptySet()

        val reference: T = context.getOlderReference() ?: run {
            return initializeFromRemote(context)
        }

        val loadedData = mutableSetOf<T>()

        if (!context.localBoundary.lower) {
            loadedData += localLoadOlder(context, reference)
            context::localBoundary.update(lower = loadedData.isEmpty())
        }

        if (!context.remoteBoundary.lower && loadedData.isEmpty()) {
            loadedData += remoteLoadOlder(context, reference)
            localSave(context, loadedData)
            context::remoteBoundary.update(lower = loadedData.isEmpty())
        }

        context.addItems(loadedData)
        return loadedData
    }
}

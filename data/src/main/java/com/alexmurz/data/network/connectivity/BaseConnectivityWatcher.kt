package com.alexmurz.data.network.connectivity

import com.alexmurz.composetexter.libcore.util.getAndUpdateValue
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicReference

/**
 * Base ConnectivityWatcher class
 * Provides subscriber management and caches currentConnectivity
 *
 * Its children need to notify it about new connectivity using onNewConnectivity
 */
internal open class BaseConnectivityWatcher : ConnectivityWatcher {
    private var currentConnectivity = AtomicReference(
        Connectivity.NOT_CONNECTED
    )

    private val connectivityChannel = MutableSharedFlow<Connectivity>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val connectivity: Connectivity
        get() = currentConnectivity.get()

    override val connectivityFlow: Flow<Connectivity> = connectivityChannel

    @Synchronized
    protected fun onNewConnectivity(connectivity: Connectivity) {
        val oldConnectivity = currentConnectivity.getAndUpdateValue { connectivity }!!
        if (oldConnectivity != connectivity) {
            connectivityChannel.tryEmit(connectivity)
        }
    }
}

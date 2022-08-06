package com.alexmurz.data.network.connectivity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

/**
 * Network watcher interface
 *
 * Allows to observe and get status of network
 */
interface ConnectivityWatcher {
    val connectivity: Connectivity
    val connectivityFlow: Flow<Connectivity>
}

data class Connectivity(
    val isConnected: Boolean,
    val hasInternet: Boolean,
    val type: ConnectivityType,
) {
    companion object {
        val NOT_CONNECTED = Connectivity(
            isConnected = false,
            hasInternet = false,
            type = ConnectivityType.NotConnected,
        )
    }
}

enum class ConnectivityType {
    NotConnected,
    ConserveTraffic,
    Unlimited,
}


/**
 * Extension to await network connection signal from ConnectivityWatcher
 *
 * Should be combined with withTimeout method of kotlin coroutines
 *
 * @see kotlinx.coroutines.withTimeout
 */
suspend fun ConnectivityWatcher.awaitNetwork(): Connectivity {
    connectivity.apply {
        if (hasInternet) return this
    }

    return connectivityFlow
        .filter { it.hasInternet }
        .first()
}

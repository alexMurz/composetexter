package com.alexmurz.composetexter.network.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.alexmurz.composetexter.util.AndroidLoggable
import com.alexmurz.composetexter.util.Loggable

//@RequiresApi(Build.VERSION_CODES.M)
internal class Api23ConnectivityWatcher(
    context: Context
) : BaseConnectivityWatcher(),
    Loggable by AndroidLoggable("ANDConnectivityWatcher") {

    private val connectivityManager =
        requireNotNull(context.getSystemService<ConnectivityManager>()) {
            "Api23ConnectivityWatcher ConnectivityManager is not available"
        }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        val networks = mutableListOf<Network>()

        override fun onAvailable(network: Network) {
            networks.add(network)
            log("New network available, networks: ${networks.size}")
        }

        override fun onLost(network: Network) {
            networks.remove(network)
            log("Network lost, networks: ${networks.size}")
            val connectivity = networks.asSequence()
                .mapNotNull {
                    connectivityManager.getNetworkCapabilities(it)?.let(::interpretCapabilities)
                }
                .maxByOrNull {
                    it.type.ordinal
                }

            onNewConnectivity(
                connectivity ?: Connectivity.NOT_CONNECTED
            )
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            log("Capabilities changed, networks: ${networks.size}")
            onNewConnectivity(interpretCapabilities(networkCapabilities))
        }
    }

    private fun interpretCapabilities(capabilities: NetworkCapabilities): Connectivity {
        val inetCapable = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val notMetered = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

        val type = if (notMetered) ConnectivityType.Unlimited
        else ConnectivityType.ConserveTraffic
        return Connectivity(
            isConnected = inetCapable,
            hasInternet = inetCapable,
            type = type,
        )
    }

    init {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            networkCallback
        )
    }
}

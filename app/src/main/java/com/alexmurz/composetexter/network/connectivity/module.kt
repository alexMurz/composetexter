package com.alexmurz.composetexter.network.connectivity

import org.koin.dsl.binds
import org.koin.dsl.module

val connectivityModule = module {
    single {
        // TODO: API21-22 implementation
        Api23ConnectivityWatcher(get())
    } binds arrayOf(ConnectivityWatcher::class)
}

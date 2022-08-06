package com.alexmurz.data.network.connectivity

import org.koin.dsl.bind
import org.koin.dsl.module

val connectivityModule = module {
    single {
        Api23ConnectivityWatcher(get())
    } bind ConnectivityWatcher::class
}
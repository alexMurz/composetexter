package com.alexmurz.composetexter.mviapp.nav_host

import io.reactivex.rxjava3.core.Observable


interface AppNav {
    val destination: Destination
    fun observeDestination(): Observable<Destination>
    fun open(destination: Destination)
}
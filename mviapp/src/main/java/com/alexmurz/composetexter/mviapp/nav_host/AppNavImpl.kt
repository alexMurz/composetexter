package com.alexmurz.composetexter.mviapp.nav_host

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class AppNavImpl: AppNav {
    private val destinationSubject = BehaviorSubject.createDefault<Destination>(Destination.TopicList)

    override val destination: Destination
        get() = destinationSubject.value!!

    override fun observeDestination(): Observable<Destination> =
        destinationSubject

    override fun open(destination: Destination) {
        destinationSubject.onNext(destination)
    }
}

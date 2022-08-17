package com.alexmurz.composetexter.mviapp.utils

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


/**
 * Run only 1 task at a time emitting busy (working) flag without request buffering
 */
fun <T : Any> Observable<out T>.runSingleTaskForBusyFlag(taskFactory: (T) -> Single<*>): Observable<Boolean> {
    return this
        .toFlowable(BackpressureStrategy.DROP)
        .flatMap(
            { data ->
                Flowable.merge(
                    Flowable.just(true),
                    taskFactory(data)
                        .map { false }
                        .toFlowable()
                )
            },
            false,
            1,
            1
        )
        .toObservable()
}

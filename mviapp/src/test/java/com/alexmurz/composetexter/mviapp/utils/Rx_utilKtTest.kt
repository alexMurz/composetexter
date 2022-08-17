package com.alexmurz.composetexter.mviapp.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Test
import java.util.concurrent.TimeUnit

class RxUtilKtTest {

    @Test
    fun runSingleTaskForBusyFlag() {
        val events = Observable
            .interval(0, 10, TimeUnit.MILLISECONDS)
            .take(100)

        val result = mutableListOf<Long>()
        val taskStateObservable = events
            .doOnNext {
                println("Try queue task $it")
            }
            .runSingleTaskForBusyFlag { v ->
                println("-> Start task $v")
                result.add(v)
                Single.timer(190, TimeUnit.MILLISECONDS)
                    .doOnSuccess {
                        println("-< End task $v")
                    }
            }
            .doOnNext {
                println("Task status $it")
            }


        require(
            taskStateObservable
                .test()
                .await(1_500, TimeUnit.MILLISECONDS)
        )

        println("Result: ${result.joinToString()}")
    }
}
package com.alexmurz.composetexter.apperror

import org.koin.dsl.binds
import org.koin.dsl.module

val appErrorModule = module {
    single {
        AppErrorHandler()
    } binds arrayOf(ErrorHandler::class)
}

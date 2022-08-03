package com.alexmurz.composetexter.apperror

import com.alexmurz.composetexter.apperror.service.AppErrorHandler
import com.alexmurz.composetexter.apperror.service.AppErrorHandlerImpl
import org.koin.dsl.binds
import org.koin.dsl.module

val appErrorModule = module {
    single {
        AppErrorHandlerImpl()
    } binds arrayOf(AppErrorHandler::class)
}

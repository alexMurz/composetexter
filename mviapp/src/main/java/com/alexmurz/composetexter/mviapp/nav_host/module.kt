package com.alexmurz.composetexter.mviapp.nav_host

import org.koin.dsl.bind
import org.koin.dsl.module

val appNavModule = module {
    single { AppNavImpl() } bind AppNav::class
}

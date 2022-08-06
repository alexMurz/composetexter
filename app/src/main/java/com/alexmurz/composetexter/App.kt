package com.alexmurz.composetexter

import android.app.Application
import android.content.Context
import com.alexmurz.composetexter.apperror.appErrorModule
import com.alexmurz.data.network.apiModule
import com.alexmurz.topic.topicModule
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        application = this
        super.onCreate()
        startKoin {
            printLogger()
            modules(appModule, apiModule, topicModule)
        }
    }

    companion object {
        lateinit var application: Application


        private val appModule = module {
            singleOf<Context> { application }

            includes(appDatabaseModule, appErrorModule)
        }
    }
}
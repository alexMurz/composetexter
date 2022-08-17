package com.alexmurz.composetexter.mviapp

import android.app.Application
import android.content.Context
import com.alexmurz.composetexter.mviapp.components.messagelist.appMessageListModule
import com.alexmurz.composetexter.mviapp.components.topiclist.appTopicListModule
import com.alexmurz.composetexter.mviapp.nav_host.appNavModule
import com.alexmurz.data.network.apiModule
import com.alexmurz.message.messageModule
import com.alexmurz.topic.topicModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        application = this
        super.onCreate()

        val uiModules = listOf(
            appTopicListModule,
            appMessageListModule
        )

        koin = startKoin {
            printLogger()
            // App modules
            modules(appModule, appDatabaseModule, appNavModule)
            // Component modules
            modules(uiModules)
            // Data modules
            modules(apiModule, topicModule, messageModule)
        }
    }

    companion object {
        lateinit var application: Application
        lateinit var koin: KoinApplication

        private val appModule = module {
            singleOf<Context> { application }
        }
    }
}
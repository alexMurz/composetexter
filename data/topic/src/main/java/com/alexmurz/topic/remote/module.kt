package com.alexmurz.topic.remote

import com.alexmurz.topic.REMOTE_QUALIFIER
import com.alexmurz.topic.api.TopicAPI
import org.koin.dsl.binds
import org.koin.dsl.module
import retrofit2.Retrofit

val topicRemoteModule = module {
    // TopicNetworkAPI
    single {
        val retrofit = get<Retrofit>()
        retrofit.create(TopicNetworkAPI::class.java)
    }

    // Bind remote
    single(REMOTE_QUALIFIER) {
        TopicRemote(get(), get())
    } binds arrayOf(
        TopicAPI.LoadNewest::class,
        TopicAPI.LoadNewer::class,
        TopicAPI.LoadOlder::class,
        TopicAPI.SaveTopics::class,
        TopicAPI.CreateTopic::class,
    )
}
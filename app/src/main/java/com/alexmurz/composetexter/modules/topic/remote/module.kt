package com.alexmurz.composetexter.modules.topic.remote

import com.alexmurz.composetexter.modules.topic.REMOTE_QUALIFIER
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
        TopicAPI.LoadUp::class,
        TopicAPI.LoadDown::class,
        TopicAPI.SaveTopics::class,
    )

    // Bind Create Topic worker
    single(REMOTE_QUALIFIER) {
        CreateTopicWorkImpl(get())
    } binds arrayOf(
        TopicAPI.CreateTopic::class
    )
}
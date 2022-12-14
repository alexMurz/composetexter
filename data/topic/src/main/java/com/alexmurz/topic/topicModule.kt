package com.alexmurz.topic

import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.remote.TopicNetworkAPI
import com.alexmurz.topic.remote.TopicRemote
import com.alexmurz.topic.actions.CreateTopicImpl
import com.alexmurz.topic.actions.InitializeImpl
import com.alexmurz.topic.actions.LoadMoreImpl
import com.alexmurz.topic.actions.UpdateImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val topicModule = module {
    // Data layer
    single { TopicEntityMapper } bind TopicEntityMapper::class
    single {
        val retrofit = get<Retrofit>()
        val api = retrofit.create(TopicNetworkAPI::class.java)
        TopicRemote(api, get())
    }

    // Bind implemented actions
    single { CreateTopicImpl(get(), get(), get()) } bind TopicAction.CreateTopic::class
    single { InitializeImpl(get(), get(), get()) } bind TopicAction.Initialize::class
    single { LoadMoreImpl(get(), get(), get()) } bind TopicAction.LoadMore::class
    single { UpdateImpl(get(), get(), get()) } bind TopicAction.Update::class

    // Include domain module
    includes(topicDomainModule)
}

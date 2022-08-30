package com.alexmurz.topic

import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.remote.TopicNetworkAPI
import com.alexmurz.topic.remote.TopicRemote
import com.alexmurz.topic.service.TopicService
import com.alexmurz.topic.use_case_impl.CreateTopicImpl
import com.alexmurz.topic.use_case_impl.InitializeImpl
import com.alexmurz.topic.use_case_impl.LoadMoreImpl
import com.alexmurz.topic.use_case_impl.UpdateImpl
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

    // Bind use cases
    single { CreateTopicImpl(get(), get(), get()) } bind TopicUseCase.CreateTopic::class
    single { InitializeImpl(get(), get()) } bind TopicUseCase.Initialize::class
    single { LoadMoreImpl(get(), get(), get()) } bind TopicUseCase.LoadMore::class
    single { UpdateImpl(get(), get(), get()) } bind TopicUseCase.Update::class

    // Bind service
    // Not sure it should done here but for now it is
    single { TopicService(get(), get(), get(), get()) }
}

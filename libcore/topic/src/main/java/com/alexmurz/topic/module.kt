package com.alexmurz.topic

import org.koin.dsl.module

val topicDomainModule = module {
    // Bind use cases
    single { TopicUseCase.Initialize(get()) }
    single { TopicUseCase.Update(get()) }
    single { TopicUseCase.LoadMore(get()) }
    single { TopicUseCase.CreateTopic(get()) }
}
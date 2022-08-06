package com.alexmurz.composetexter.modules.topic

import com.alexmurz.composetexter.modules.topic.mapper.TopicEntityMapper
import com.alexmurz.composetexter.modules.topic.remote.topicRemoteModule
import com.alexmurz.composetexter.modules.topic.storage.RoomTopicStorage
import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.service.TopicService
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal val LOCAL_QUALIFIER = named("local")
internal val REMOTE_QUALIFIER = named("remote")

val topicModule = module {
    single { TopicEntityMapper } bind TopicEntityMapper::class

    // Bind locals
    single(LOCAL_QUALIFIER) {
        RoomTopicStorage(get(), get())
    } binds arrayOf(
        TopicAPI.LoadNewest::class,
        TopicAPI.LoadUp::class,
        TopicAPI.LoadDown::class,
        TopicAPI.SaveTopics::class,
    )

    includes(topicRemoteModule)


    // Bind service
    single {
        TopicService(
            remoteLoadDown = get(REMOTE_QUALIFIER),
            remoteLoadUp = get(REMOTE_QUALIFIER),
            remoteLoadNewest = get(REMOTE_QUALIFIER),
            localLoadDownPage = get(LOCAL_QUALIFIER),
            localLoadNewest = get(LOCAL_QUALIFIER),
            localSaveTopics = get(LOCAL_QUALIFIER),
            createNewTopic = get(REMOTE_QUALIFIER),
        )
    }

}

package com.alexmurz.topic

import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.remote.topicRemoteModule
import com.alexmurz.topic.service.TopicService
import com.alexmurz.topic.storage.RoomTopicStorage
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
        TopicAPI.LoadNewer::class,
        TopicAPI.LoadOlder::class,
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

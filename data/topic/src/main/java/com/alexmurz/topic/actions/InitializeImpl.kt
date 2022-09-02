package com.alexmurz.topic.actions

import com.alexmurz.data.use_case.initialize
import com.alexmurz.topic.TopicAction
import com.alexmurz.topic.TopicsContext
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.dao.loadNewestHandle
import com.alexmurz.topic.dao.saveHandle
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.remote.TopicRemote
import com.alexmurz.topic.remote.loadNewestHandle

internal class InitializeImpl(
    mapper: TopicEntityMapper,
    dao: TopicDao,
    remote: TopicRemote,
) : TopicAction.Initialize {
    private val localLoadNewest = dao.loadNewestHandle(mapper)
    private val localSave = dao.saveHandle(mapper)

    private val remoteLoadNewest = remote.loadNewestHandle()

    override suspend fun initialize(context: TopicsContext): Set<Topic> =
        context.initialize(
            localLoadNewest = localLoadNewest,
            remoteLoadNewest = remoteLoadNewest,
            localSave = localSave,
        )
}

package com.alexmurz.topic.actions

import com.alexmurz.data.use_case.initialize
import com.alexmurz.topic.TopicAction
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.dao.loadNewestHandle
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.service.TopicServiceContext

internal class InitializeImpl(
    mapper: TopicEntityMapper,
    dao: TopicDao,
) : TopicAction.Initialize {
    private val localLoadNewest = dao.loadNewestHandle(mapper)

    override suspend fun initialize(context: TopicServiceContext): Set<Topic> =
        context.initialize(localLoadNewest)
}

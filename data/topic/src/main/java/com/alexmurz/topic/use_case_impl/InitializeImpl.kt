package com.alexmurz.topic.use_case_impl

import com.alexmurz.data.use_case.initialize
import com.alexmurz.topic.TopicUseCase
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.dao.loadNewestHandle
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.service.TopicServiceContext

internal class InitializeImpl(
    mapper: TopicEntityMapper,
    dao: TopicDao,
) : TopicUseCase.Initialize {
    private val localLoadNewest = dao.loadNewestHandle(mapper)

    override suspend fun initialize(context: TopicServiceContext): Set<Topic> =
        context.initialize(localLoadNewest)
}

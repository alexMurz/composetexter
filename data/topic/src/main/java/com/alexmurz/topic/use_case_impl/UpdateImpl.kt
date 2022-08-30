package com.alexmurz.topic.use_case_impl

import com.alexmurz.data.use_case.loadNewer
import com.alexmurz.topic.TopicUseCase
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.dao.loadNewestHandle
import com.alexmurz.topic.dao.saveHandle
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.remote.TopicRemote
import com.alexmurz.topic.remote.loadNewerHandle
import com.alexmurz.topic.remote.loadNewestHandle
import com.alexmurz.topic.service.TopicServiceContext

internal class UpdateImpl(
    mapper: TopicEntityMapper,
    dao: TopicDao,
    remote: TopicRemote,
) : TopicUseCase.Update {
    private val localLoadNewest = dao.loadNewestHandle(mapper)
    private val localSave = dao.saveHandle(mapper)

    private val remoteLoadNewest = remote.loadNewestHandle()
    private val remoteLoadNewer = remote.loadNewerHandle()

    override suspend fun update(context: TopicServiceContext): Set<Topic> = context.loadNewer(
        localLoadNewest = localLoadNewest,
        remoteLoadNewest = remoteLoadNewest,
        remoteLoadNewer = remoteLoadNewer,
        localSave = localSave
    )
}

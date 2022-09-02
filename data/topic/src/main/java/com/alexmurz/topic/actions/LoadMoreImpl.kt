package com.alexmurz.topic.actions

import com.alexmurz.data.use_case.loadOlder
import com.alexmurz.topic.TopicAction
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.dao.loadNewestHandle
import com.alexmurz.topic.dao.loadOlderHandle
import com.alexmurz.topic.dao.saveHandle
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.remote.TopicRemote
import com.alexmurz.topic.remote.loadNewestHandle
import com.alexmurz.topic.remote.loadOlderHandle
import com.alexmurz.topic.TopicsContext

internal class LoadMoreImpl(
    mapper: TopicEntityMapper,
    dao: TopicDao,
    remote: TopicRemote,
) : TopicAction.LoadMore {

    private val localLoadNewest = dao.loadNewestHandle(mapper)
    private val localLoadOlder = dao.loadOlderHandle(mapper)
    private val localSave = dao.saveHandle(mapper)

    private val remoteLoadNewest = remote.loadNewestHandle()
    private val remoteLoadOlder = remote.loadOlderHandle()

    override suspend fun loadMore(context: TopicsContext): Set<Topic> = context.loadOlder(
        localLoadNewest = localLoadNewest,
        localLoadOlder = localLoadOlder,
        remoteLoadNewest = remoteLoadNewest,
        remoteLoadOlder = remoteLoadOlder,
        localSave = localSave,
    )
}

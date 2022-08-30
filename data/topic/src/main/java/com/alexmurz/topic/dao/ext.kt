/**
 * Set of DAO extensions to provide implementation for arguments of set_based_loading.kt extension library
 */

package com.alexmurz.topic.dao

import com.alexmurz.data.use_case.LoadNewerHandle
import com.alexmurz.data.use_case.LoadNewestHandle
import com.alexmurz.data.use_case.LoadOlderHandle
import com.alexmurz.data.use_case.SaveHandle
import com.alexmurz.topic.mapper.TopicEntityMapper
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.referenceDate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal fun TopicDao.loadNewestHandle(mapper: TopicEntityMapper): LoadNewestHandle<Topic> = {
    getNewestTopics(it.limit).mapTo(mutableSetOf(), mapper::fromEntity)
}

internal fun TopicDao.loadNewerHandle(mapper: TopicEntityMapper): LoadNewerHandle<Topic> =
    { it, reference ->
        loadUp(reference.referenceDate.timestamp, it.limit).mapTo(
            mutableSetOf(),
            mapper::fromEntity
        )
    }

internal fun TopicDao.loadOlderHandle(mapper: TopicEntityMapper): LoadOlderHandle<Topic> =
    { it, reference ->
        loadDown(reference.referenceDate.timestamp, it.limit).mapTo(
            mutableSetOf(),
            mapper::fromEntity
        )
    }

internal fun TopicDao.saveHandle(mapper: TopicEntityMapper): SaveHandle<Topic> = { _, it ->
    // Always save and without suspending
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch {
        saveTopics(it.map(mapper::toEntity))
    }
}

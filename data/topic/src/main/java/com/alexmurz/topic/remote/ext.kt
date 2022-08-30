/**
 * Set of extensions to provide implementation for arguments of set_based_loading.kt extension library
 */

package com.alexmurz.topic.remote

import com.alexmurz.data.use_case.LoadNewerHandle
import com.alexmurz.data.use_case.LoadNewestHandle
import com.alexmurz.data.use_case.LoadOlderHandle
import com.alexmurz.topic.model.Topic
import com.alexmurz.topic.referenceDate


internal fun TopicRemote.loadNewestHandle(): LoadNewestHandle<Topic> = {
    loadNewestTopics(it.limit)
}

internal fun TopicRemote.loadNewerHandle(): LoadNewerHandle<Topic> = { it, reference ->
    loadUpTopics(reference.referenceDate, it.limit)
}

internal fun TopicRemote.loadOlderHandle(): LoadOlderHandle<Topic> = { it, reference ->
    loadDownTopics(reference.referenceDate, it.limit)
}

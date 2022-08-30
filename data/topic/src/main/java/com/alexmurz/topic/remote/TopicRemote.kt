package com.alexmurz.topic.remote

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.data.network.BaseRemote
import com.alexmurz.data.network.connectivity.ConnectivityWatcher
import com.alexmurz.topic.model.Topic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopicRemote(
    private val api: TopicNetworkAPI,
    connectivityWatcher: ConnectivityWatcher,
) : BaseRemote(connectivityWatcher) {

    suspend fun loadNewestTopics(limit: Int): Set<Topic> = checkGET {
        withContext(Dispatchers.IO) {
            api.getNewest(limit).mapTo(mutableSetOf()) { it.toTopic() }
        }
    }

    suspend fun loadDownTopics(date: CATime, limit: Int): Set<Topic> = checkGET {
        withContext(Dispatchers.IO) {
            api.getOlder(limit, date.timestamp).mapTo(mutableSetOf()) { it.toTopic() }
        }
    }

    suspend fun loadUpTopics(date: CATime, limit: Int): Set<Topic> = checkGET {
        withContext(Dispatchers.IO) {
            api.getNewer(limit, date.timestamp).mapTo(mutableSetOf()) { it.toTopic() }
        }
    }

    suspend fun createTopic(title: String, message: String): Topic = checkPOST {
        withContext(Dispatchers.IO) {
            api.create(title, message).toTopic()
        }
    }
}

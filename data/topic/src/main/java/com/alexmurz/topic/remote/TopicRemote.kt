package com.alexmurz.topic.remote

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.data.network.BaseRemote
import com.alexmurz.data.network.connectivity.ConnectivityWatcher
import com.alexmurz.topic.api.TopicAPI
import com.alexmurz.topic.model.Topic

class TopicRemote(
    private val api: TopicNetworkAPI,
    connectivityWatcher: ConnectivityWatcher,
) : BaseRemote(connectivityWatcher),
    TopicAPI.LoadNewest,
    TopicAPI.LoadOlder,
    TopicAPI.LoadNewer,
    TopicAPI.CreateTopic {

    override suspend fun loadNewestTopics(limit: Int): Set<Topic> = checkGET {
        api.getNewest(limit).mapTo(mutableSetOf()) { it.toTopic() }
    }

    override suspend fun loadDownTopics(date: CATime, limit: Int): Set<Topic> = checkGET {
        api.getOlder(limit, date.timestamp).mapTo(mutableSetOf()) { it.toTopic() }
    }

    override suspend fun loadUpTopics(date: CATime, limit: Int): Set<Topic> = checkGET {
        api.getNewer(limit, date.timestamp).mapTo(mutableSetOf()) { it.toTopic() }
    }

    override suspend fun createTopic(title: String, message: String): Topic = checkPOST {
        api.create(title, message).toTopic()
    }
}

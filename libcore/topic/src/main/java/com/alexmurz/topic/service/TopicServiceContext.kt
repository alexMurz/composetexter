package com.alexmurz.topic.service

import com.alexmurz.composetexter.libcore.service.set_service.CommonSetBasedServiceContext
import com.alexmurz.topic.model.Topic
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicReference

/**
 * Hot cache and context holder for `TopicService`
 */
class TopicServiceContext(limit: Int): CommonSetBasedServiceContext<Topic>(limit) {
    override fun getNewerReference(): Topic? = data.maxByOrNull { it.date.timestamp }
    override fun getOlderReference(): Topic? = data.minByOrNull { it.date.timestamp }
}

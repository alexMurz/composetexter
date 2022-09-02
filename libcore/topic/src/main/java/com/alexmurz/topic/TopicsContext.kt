package com.alexmurz.topic

import com.alexmurz.composetexter.libcore.service.set_service.AbstractSetBasedContext
import com.alexmurz.topic.model.Topic

/**
 * Hot cache and context holder for `TopicService`
 */
class TopicsContext(limit: Int): AbstractSetBasedContext<Topic>(limit) {
    override fun getNewerReference(): Topic? = data.maxByOrNull { it.date.timestamp }
    override fun getOlderReference(): Topic? = data.minByOrNull { it.date.timestamp }
}

package com.alexmurz.topic.mapper

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.model.TopicEntity
import com.alexmurz.topic.model.Topic

interface TopicEntityMapper {
    fun fromEntity(entity: TopicEntity): Topic

    fun toEntity(topic: Topic): TopicEntity

    companion object : TopicEntityMapper {
        override fun fromEntity(entity: TopicEntity): Topic = Topic(
            id = entity.id,
            date = CATime.of(entity.date),
            title = entity.title,
            message = entity.message,
            // TODO-topic: attachments
            attachments = emptyList(),
        )

        override fun toEntity(topic: Topic): TopicEntity = TopicEntity(
            id = topic.id,
            date = topic.date.timestamp,
            title = topic.title,
            message = topic.message,
        )
    }
}

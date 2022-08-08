package com.alexmurz.message.mapper

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.message.model.MessageEntity
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent

interface MessageEntityMapper {
    fun fromEntity(entity: MessageEntity): Message

    fun toEntity(parent: MessageChainParent, message: Message): MessageEntity

    companion object : MessageEntityMapper {
        override fun fromEntity(entity: MessageEntity): Message = Message(
            id = entity.id,
            message = entity.message,
            dateCreated = CATime.of(entity.dateCreated),
            dateUpdated = CATime.of(entity.dateUpdated),
        )

        override fun toEntity(parent: MessageChainParent, message: Message): MessageEntity = MessageEntity(
            id = message.id,
            parentId = parent.id,
            message = message.message,
            dateCreated = message.dateCreated.timestamp,
            dateUpdated = message.dateUpdated.timestamp,
        )
    }
}

package com.alexmurz.messages.service

import com.alexmurz.composetexter.libcore.service.set_service.AbstractSetBasedContext
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent

class MessageContext(
    limit: Int,
    val parent: MessageChainParent,
) : AbstractSetBasedContext<Message>(limit) {
    override fun getNewerReference(): Message? = data.maxByOrNull { it.dateCreated.timestamp }
    override fun getOlderReference(): Message? = data.minByOrNull { it.dateCreated.timestamp }
}

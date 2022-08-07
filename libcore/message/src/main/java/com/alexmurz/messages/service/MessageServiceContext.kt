package com.alexmurz.messages.service

import com.alexmurz.composetexter.libcore.service.set_service.CommonSetBasedServiceContext
import com.alexmurz.composetexter.libcore.util.getAndUpdateValue
import com.alexmurz.messages.model.Message
import com.alexmurz.messages.model.MessageChainParent
import java.util.concurrent.atomic.AtomicReference

class MessageServiceContext(
    limit: Int,
    val parent: MessageChainParent,
): CommonSetBasedServiceContext<Message>(limit) {
    override fun getNewerReference(): Message? = data.maxByOrNull { it.dateCreated.timestamp }
    override fun getOlderReference(): Message? = data.minByOrNull { it.dateCreated.timestamp }
}

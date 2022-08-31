package com.alexmurz.topic.model

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.libcore.attachment.Attachment
import com.alexmurz.composetexter.libcore.util.quickCompare
import com.alexmurz.messages.model.MessageChainParentType

class Topic(
    val id: Long,
    val date: CATime,
    val title: String,
    val message: String,
    val attachments: List<Attachment>
) {

    /**
     * Ordering argument. Ascending
     */
    val order: Long = Long.MAX_VALUE - date.timestamp

    val messageChainParent by lazy {
        MessageChainParentType.Topic.create(id)
    }

    fun isSameContent(other: Any?): Boolean = quickCompare(other) {
        when {
            id != it.id -> false
            date != it.date -> false
            title != it.title -> false
            message != it.message -> false
            attachments != it.attachments -> false
            else -> true
        }
    }

    override fun toString() =
        "Topic(id=$id, datetime=$date, title=$title, message=$message, attachments=$attachments)"

    override fun equals(other: Any?): Boolean = quickCompare(other) {
        id == it.id
    }

    override fun hashCode() = id.hashCode()
}
package com.alexmurz.topic.model

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.libcore.attachment.Attachment
import java.util.*

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

    fun isSameContent(other: Any?) = when (other) {
        is Topic -> id == other.id
            && date == other.date
            && title == other.title
            && message == other.message
            && attachments == other.attachments
        else -> false
    }

    override fun toString() =
        "Topic(id=$id, datetime=$date, title=$title, message=$message, attachments=$attachments)"

    override fun equals(other: Any?) = when (other) {
        is Topic -> id == other.id
        else -> false
    }

    override fun hashCode() = id.hashCode()
}
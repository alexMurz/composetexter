package com.alexmurz.messages.model

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.composetexter.libcore.util.quickCompare

class Message(
    val id: Int,

    val message: String,
    val dateCreated: CATime,
    val dateUpdated: CATime,
) {
    fun isSameContent(other: Any?): Boolean = quickCompare(other) {
        when {
            id != it.id -> false
            message != it.message -> false
            dateCreated != it.dateCreated -> false
            dateUpdated != it.dateUpdated -> false
            else -> true
        }
    }

    override fun equals(other: Any?): Boolean = quickCompare(other) {
        id == it.id
    }

    override fun hashCode(): Int = id.hashCode()
}

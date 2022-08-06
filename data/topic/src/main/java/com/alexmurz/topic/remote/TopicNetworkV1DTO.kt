package com.alexmurz.topic.remote

import com.alexmurz.composetexter.libcore.CATime
import com.alexmurz.topic.model.Topic
import com.google.gson.annotations.SerializedName

// topic/v1 network data object
class TopicNetworkV1DTO {
    @SerializedName("id")
    var id: Long = 0

    @SerializedName("date")
    var date: Long = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("message")
    var message: String? = null

}

fun TopicNetworkV1DTO.toTopic() = Topic(
    id = id,
    date = CATime.of(date),
    title = title ?: "",
    message = message ?: "",
    attachments = emptyList(),
)
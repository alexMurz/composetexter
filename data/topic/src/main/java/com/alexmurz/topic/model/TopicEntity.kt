package com.alexmurz.topic.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "topics"
)
data class TopicEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: Long,

    @ColumnInfo(name = "date", index = true)
    var date: Long,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "message")
    var message: String,
)

package com.alexmurz.message.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
)
class MessageEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "parent_id", index = true)
    val parentId: Long,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "date_created")
    val dateCreated: Long,

    @ColumnInfo(name = "date_updated")
    val dateUpdated: Long,
)

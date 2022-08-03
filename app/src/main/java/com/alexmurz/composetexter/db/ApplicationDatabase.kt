package com.alexmurz.composetexter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexmurz.composetexter.modules.topic.dao.TopicDao
import com.alexmurz.composetexter.modules.topic.model.TopicEntity

@Database(
    entities = [TopicEntity::class],
    version = 1,
)
abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun topicDao(): TopicDao
}
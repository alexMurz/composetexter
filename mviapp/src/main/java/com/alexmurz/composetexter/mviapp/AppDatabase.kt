package com.alexmurz.composetexter.mviapp

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexmurz.message.dao.MessageDao
import com.alexmurz.message.model.MessageEntity
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.model.TopicEntity
import org.koin.dsl.module

@Database(
    version = 1,
    entities = [
        TopicEntity::class,
        MessageEntity::class,
    ],
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun messageDao(): MessageDao
}

internal val appDatabaseModule = module {
    single {
        Room
            .databaseBuilder(get(), ApplicationDatabase::class.java, "db")
            .build()
    }

    single { get<ApplicationDatabase>().messageDao() }
    single { get<ApplicationDatabase>().topicDao() }
}

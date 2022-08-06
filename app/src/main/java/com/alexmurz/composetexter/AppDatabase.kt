package com.alexmurz.composetexter

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexmurz.topic.dao.TopicDao
import com.alexmurz.topic.model.TopicEntity
import org.koin.dsl.module

@Database(
    entities = [TopicEntity::class],
    version = 1,
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
}

internal val appDatabaseModule = module {
    single {
        Room
            .databaseBuilder(get(), ApplicationDatabase::class.java, "db")
            .build()
    }

    single {
        val db: ApplicationDatabase = get()
        db.topicDao()
    }
}

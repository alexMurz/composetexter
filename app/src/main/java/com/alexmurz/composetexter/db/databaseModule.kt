package com.alexmurz.composetexter.db

import androidx.room.Room
import org.koin.dsl.module
import java.io.File

val databaseModule = module {
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

package com.alexmurz.composetexter.modules.topic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.alexmurz.composetexter.modules.topic.model.TopicEntity

@Dao
interface TopicDao {

    @Query("SELECT * FROM topics ORDER BY date DESC LIMIT :limit")
    fun getNewestTopics(limit: Int): List<TopicEntity>

    @Query("""
SELECT * FROM topics 
WHERE date < :date
ORDER BY date DESC 
LIMIT :limit
""")
    fun loadDown(date: Long, limit: Int): List<TopicEntity>

    @Query("""
SELECT * FROM topics 
WHERE date > :date
ORDER BY date ASC 
LIMIT :limit
""")
    fun loadUp(date: Long, limit: Int): List<TopicEntity>

    @Insert(onConflict = REPLACE)
    fun saveTopics(topics: List<TopicEntity>)

}
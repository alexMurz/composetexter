package com.alexmurz.message.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexmurz.message.model.MessageEntity

private const val SORT_FIELD = "date_created"

@Dao
interface MessageDao {
    @Query(
        """
SELECT * FROM messages 
WHERE parent_id == :parentId
ORDER BY $SORT_FIELD DESC 
LIMIT :limit"""
    )
    fun newest(parentId: Long, limit: Int): List<MessageEntity>

    @Query(
        """
SELECT * FROM messages 
WHERE parent_id == :parentId AND $SORT_FIELD < :date
ORDER BY $SORT_FIELD DESC 
LIMIT :limit
"""
    )
    fun loadOlder(parentId: Long, date: Long, limit: Int): List<MessageEntity>

    @Query(
        """
SELECT * FROM messages 
WHERE parent_id == :parentId AND $SORT_FIELD > :date
ORDER BY $SORT_FIELD ASC 
LIMIT :limit
"""
    )
    fun loadNewer(parentId: Long, date: Long, limit: Int): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(topics: List<MessageEntity>)
}

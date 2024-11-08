package com.reza.storyapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStory(story: List<StoryEntity>)

    @Query("DELETE FROM story")
    suspend fun deleteAll()

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM story WHERE id = :id")
    fun getStoryById(id: String): LiveData<StoryEntity>

}
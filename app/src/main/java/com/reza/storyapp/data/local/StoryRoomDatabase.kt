package com.reza.storyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryRoomDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryRoomDatabase? = null
        fun getInstance(context: Context): StoryRoomDatabase =
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                StoryRoomDatabase::class.java,
                "story.db"
            ).build()
    }
}
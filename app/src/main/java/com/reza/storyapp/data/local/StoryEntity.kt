package com.reza.storyapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class StoryEntity(

    @field:ColumnInfo("photoUrl")
    val photoUrl: String? = null,

    @field:ColumnInfo("createdAt")
    val createdAt: String? = null,

    @field:ColumnInfo("name")
    val name: String? = null,

    @field:ColumnInfo("description")
    val description: String? = null,

    @field:ColumnInfo("lon")
    val lon: Float? = null,

    @field:PrimaryKey(autoGenerate = false)
    val id: String,

    @field:SerializedName("lat")
    val lat: Float? = null
)
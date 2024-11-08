package com.reza.storyapp

import com.reza.storyapp.data.local.StoryEntity

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "date + $i",
                "name $i",
                "desc $i",
                0.2F,
                "id $i",
                0.5F
            )
            items.add(quote)
        }
        return items
    }
}
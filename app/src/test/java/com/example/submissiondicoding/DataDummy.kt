package com.example.submissiondicoding

import com.example.submissiondicoding.api.response.StoryItem

object DataDummy {

    fun generateDummyStory(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryItem(
                "photo $i",
                "$i",
                "name + $i",
                "description + $i",
                123,
                i.toString(),
                123

            )
            items.add(quote)
        }
        return items
    }

}
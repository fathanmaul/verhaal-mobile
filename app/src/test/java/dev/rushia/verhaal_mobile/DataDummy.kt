package dev.rushia.verhaal_mobile

import dev.rushia.verhaal_mobile.data.remote.response.StoryItem

object DataDummy {
    fun generateDummyQuoteResponse(): List<StoryItem> {
        val items: MutableList<StoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = StoryItem(
                id = i.toString(),
                name = "Name $i",
                description = "Description $i",
                photoUrl = "PhotoUrl $i",
                lat = i.toDouble(),
                lon = i.toDouble(),
                createdAt = "2021-08-15T00:00:00.000Z"
            )
            items.add(story)
        }
        return items
    }
}
package com.angelomarzo.hackernews.data.model

import java.time.Instant


fun HackerNewsItem.toStory() : Story? {
    if(this.type != HackerNewsItemType.STORY)
        return null

    return Story(
        id = this.id,
        by = this.by,
        time = this.time?.let { Instant.ofEpochSecond(it) },
        title = this.title,
        url = this.url,
        score = this.score,
        descendants = this.descendants,
        text = this.text
    )
}
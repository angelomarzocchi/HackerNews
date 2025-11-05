package com.angelomarzo.hackernews.data.model

enum class HackerNewsItemType(val value: String) {
    JOB("job"),
    STORY("story"),
    COMMENT("comment"),
    POLL("poll"),
    POLLOPT("pollopt");
}

data class HackerNewsItem(
    val id: Int,
    val deleted: Boolean?,
    val type: HackerNewsItemType?,
    val by: String?,
    val time: Long?,
    val text: String?,
    val dead: Boolean?,
    val parent: Long?,
    val poll: Long?,
    val kids: List<Int>?,
    val url: String?,
    val score: Int?,
    val title: String?,
    val parts: List<Int>?,
    val descendants: Int?,
)
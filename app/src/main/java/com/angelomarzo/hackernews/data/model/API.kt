package com.angelomarzo.hackernews.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HackerNewsItemType {
    @SerialName("job")
    JOB,

    @SerialName("story")
    STORY,

    @SerialName("comment")
    COMMENT,

    @SerialName("poll")
    POLL,

    @SerialName("pollopt")
    POLLOPT;
}

@Serializable
data class HackerNewsItem(
    val id: Int,
    val deleted: Boolean? = null,
    val type: HackerNewsItemType? = null,
    val by: String? = null,
    val time: Long? = null,
    val text: String? = null,
    val dead: Boolean?= null,
    val parent: Long? = null,
    val poll: Long? = null,
    val kids: List<Int>? = null,
    val url: String? = null,
    val score: Int? = null,
    val title: String? = null,
    val parts: List<Int>? = null,
    val descendants: Int?= null
)
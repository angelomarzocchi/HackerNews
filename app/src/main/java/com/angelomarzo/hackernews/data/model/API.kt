package com.angelomarzo.hackernews.data.model

enum class ItemType(val value: String) {
    JOB("job"),
    STORY("story"),
    COMMENT("comment"),
    POLL("poll"),
    POLLOPT("pollopt");
}

data class Item(
    val id: Long,
    val deleted: Boolean?,
    val type: ItemType,
    val by: String?,
    val time: Long?,
    val text: String?,
    val dead: Boolean?,
    val parent: Long?,
    val poll: Long?,
    val kids: List<Long>?,
    val url: String?,
    val score: Int?,
    val title: String?,
    val parts: List<Long>?,
    val descendants: Int?,


)
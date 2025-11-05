package com.angelomarzo.hackernews.data.model

import java.time.Instant


data class Story(
    val id: Int,
    val by: String?,
    val time: Instant?,
    val title: String?,
    val url: String?,
    val score: Int?,
    val descendants: Int?,
    val text: String?
)

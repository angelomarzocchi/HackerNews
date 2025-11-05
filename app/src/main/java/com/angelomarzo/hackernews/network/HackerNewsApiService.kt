package com.angelomarzo.hackernews.network

import com.angelomarzo.hackernews.data.model.HackerNewsItem
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApiService {

    @GET(value = "topstories.json")
    suspend fun getTopStories(): List<Int>

    @GET(value = "newstories.json")
    suspend fun getNewStories(): List<Int>

    @GET(value = "beststories.json")
    suspend fun getBestStories(): List<Int>

    @GET(value = "item/{itemId}.json")
    suspend fun getStory(@Path(value = "itemId") itemId: Int): HackerNewsItem


}
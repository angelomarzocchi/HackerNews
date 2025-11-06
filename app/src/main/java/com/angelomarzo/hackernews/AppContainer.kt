package com.angelomarzo.hackernews

import android.content.Context
import com.angelomarzo.hackernews.data.HackerNewsRepository
import com.angelomarzo.hackernews.data.NetworkHackerNewsRepository
import com.angelomarzo.hackernews.network.HackerNewsApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val hackerNewsRepository: HackerNewsRepository
    val version : String
}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val hackerNewsRepository: HackerNewsRepository by lazy {
        NetworkHackerNewsRepository(
            hackerNewsApi = retrofitService
        )
    }

    override val version = "v0"

    private val baseUrl =
        " https://hacker-news.firebaseio.com/${version}/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))

    private val retrofitService: HackerNewsApiService by lazy {
        retrofit
            .baseUrl(baseUrl)
            .build()
            .create(HackerNewsApiService::class.java)
    }

}
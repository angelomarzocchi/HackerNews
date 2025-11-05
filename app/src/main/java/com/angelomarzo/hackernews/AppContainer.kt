package com.angelomarzo.hackernews

import android.content.Context
import com.angelomarzo.hackernews.data.HackerNewsRepository
import com.angelomarzo.hackernews.data.NetworkHackerNewsRepository
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val hackerNewsRepository: HackerNewsRepository
    val version : String
}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val hackerNewsRepository: HackerNewsRepository by lazy {
        NetworkHackerNewsRepository(
            hackerNewsApi =
        )
    }

    override val version = "v0"

    private val baseUrl =
        " https://hacker-news.firebaseio.com/${version}"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json)

}
package com.angelomarzo.hackernews.data

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.angelomarzo.hackernews.data.model.Story
import com.angelomarzo.hackernews.data.model.toStory
import com.angelomarzo.hackernews.network.HackerNewsApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.IOException

typealias StoryIdsFetcher = suspend () -> List<Int>

class HackerNewsPagingSource(
    private val apiService: HackerNewsApiService,
    private val storyIds: List<Int>
): PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            return state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {

        val position = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val toIndex = (position + pageSize).coerceAtMost(storyIds.size)
            if (position >= toIndex) {
                return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            }

            val pageIds = storyIds.subList(position, toIndex)

            val stories = coroutineScope {
                pageIds.map { id ->
                    async {
                        try {
                            apiService.getStory(id).toStory()
                        } catch (e: Exception) {
                            Log.e("HackerNewsPagingSource","Failed to fetch or map story item $id: ${e.message}")
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            val nextKey = if (toIndex >= storyIds.size) null else toIndex
            val prevKey = if (position == 0) null else position - pageSize

            LoadResult.Page(
                data = stories,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }
}
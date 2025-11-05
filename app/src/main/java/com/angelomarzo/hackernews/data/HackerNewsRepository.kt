package com.angelomarzo.hackernews.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.angelomarzo.hackernews.data.model.HackerNewsItem
import com.angelomarzo.hackernews.data.model.Story
import com.angelomarzo.hackernews.data.model.toStory
import com.angelomarzo.hackernews.network.HackerNewsApiService
import kotlinx.coroutines.flow.Flow
import java.io.IOException

interface HackerNewsRepository {
     fun getTopStories(): Flow<PagingData<Story>>
     fun getNewStories(): Flow<PagingData<Story>>
     fun getBestStories(): Flow<PagingData<Story>>
    suspend fun getStory(id: Int): Story?
}

class NetworkHackerNewsRepository(
    private val hackerNewsApi: HackerNewsApiService
) : HackerNewsRepository {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getTopStories(): Flow<PagingData<Story>>{
        return getPagedStories { hackerNewsApi.getTopStories() }
    }

    override fun getNewStories(): Flow<PagingData<Story>> {
        return getPagedStories { hackerNewsApi.getNewStories() }
    }

    override fun getBestStories(): Flow<PagingData<Story>> {
       return getPagedStories { hackerNewsApi.getBestStories() }
    }

    override suspend fun getStory(id: Int): Story? {
        return hackerNewsApi.getStory(id)
            .toStory()
    }

    private fun getPagedStories(idsFetcher: suspend () -> List<Int>): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                object : PagingSource<Int, Story>() {
                    private var storyIds: List<Int>? = null

                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
                        val ids = storyIds ?: try {
                            idsFetcher().also { storyIds = it }
                        } catch (e: IOException) {
                            return LoadResult.Error(e)
                        } catch (e: Exception) {
                            return LoadResult.Error(e)
                        }
                        return HackerNewsPagingSource(hackerNewsApi, ids).load(params)
                    }

                    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
                        return null
                    }
                }
            }
        ).flow
    }



}



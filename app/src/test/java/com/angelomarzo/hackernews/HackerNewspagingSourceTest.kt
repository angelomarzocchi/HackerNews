package com.angelomarzo.hackernews

import android.util.Log
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.paging.PagingSource
import com.angelomarzo.hackernews.data.HackerNewsPagingSource
import com.angelomarzo.hackernews.data.model.HackerNewsItem
import com.angelomarzo.hackernews.data.model.HackerNewsItemType
import com.angelomarzo.hackernews.data.model.Story
import com.angelomarzo.hackernews.data.model.toStory
import com.angelomarzo.hackernews.network.HackerNewsApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException


class HackerNewsPagingSourceTest {

    private val mockApiService: HackerNewsApiService = mockk()
    private val fakeStoryIds = (1..100).toList()

    private val fakeStories = fakeStoryIds.map { id ->
        HackerNewsItem(
            id = id,
            title = "Title $id",
            score = id * 10,
            descendants = id * 5,
            type = HackerNewsItemType.STORY
            )
    }

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `load - returns success result with correct data on first load`() = runTest {

        fakeStories.subList(0, 20).forEach { item ->
            coEvery { mockApiService.getStory(item.id) } returns item
        }

        val pagingSource = HackerNewsPagingSource(mockApiService, fakeStoryIds)
        val expectedPageSize = 20

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = expectedPageSize,
                placeholdersEnabled = false
            )
        )

        val expectedData = fakeStories.subList(0, 20).mapNotNull { it.toStory() }
        val expected = PagingSource.LoadResult.Page(
            data = expectedData,
            prevKey = null,
            nextKey = 20
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `load - returns empty page when all individual api calls fail`() = runTest {
        coEvery { mockApiService.getStory(any()) } throws IOException("Network error")

        val pagingSource = HackerNewsPagingSource(mockApiService, fakeStoryIds)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page<Int, Story>(
            data = emptyList(),
            prevKey = null,
            nextKey = 20
        )
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `load - returns correct nextKey when not at the end of the list`() = runTest {
        fakeStories.subList(20, 40).forEach { item ->
            coEvery { mockApiService.getStory(item.id) } returns item
        }
        val pagingSource = HackerNewsPagingSource(mockApiService, fakeStoryIds)


        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 20,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )


        val page = result as PagingSource.LoadResult.Page
        Assert.assertEquals(40, page.nextKey)
    }

    @Test
    fun` load - returns when not the end of the list`() = runTest {
        val pagingSource = HackerNewsPagingSource(mockApiService, fakeStoryIds)

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 100,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        val expected = PagingSource.LoadResult.Page<Int, Story>(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )


        Assert.assertEquals(expected, result)


    }

}

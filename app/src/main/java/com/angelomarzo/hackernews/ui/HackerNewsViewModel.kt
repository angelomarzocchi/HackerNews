package com.angelomarzo.hackernews.ui

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.angelomarzo.hackernews.HackerNewsApplication
import com.angelomarzo.hackernews.data.HackerNewsRepository
import com.angelomarzo.hackernews.data.model.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

enum class StoryType {
    TOP,
    NEW,
    BEST
}

sealed class HackerNewsEvent {
    data class OpenUrl(val url: String) : HackerNewsEvent()
}

class HackerNewsViewModel(
    private val repository: HackerNewsRepository
): ViewModel() {

    private val _storyType = MutableStateFlow(StoryType.TOP)
    val storyType: StateFlow<StoryType> = _storyType

    private val _events = Channel<HackerNewsEvent>()
    val events = _events.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val stories: Flow<PagingData<Story>> = storyType.flatMapLatest { type ->
        when (type) {
            StoryType.TOP -> repository.getTopStories()
            StoryType.NEW -> repository.getNewStories()
            StoryType.BEST -> repository.getBestStories()
        }
    }.cachedIn(viewModelScope)

    fun selectStoryType(type: StoryType) {
        _storyType.value = type
    }

    fun onStoryClick(story: Story) {
        story.url?.let { url ->
            viewModelScope.launch {
                _events.send(HackerNewsEvent.OpenUrl(url))
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HackerNewsApplication)
                HackerNewsViewModel(application.container.hackerNewsRepository)
            }
        }
    }

}
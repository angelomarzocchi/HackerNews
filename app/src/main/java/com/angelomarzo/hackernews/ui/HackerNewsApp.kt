package com.angelomarzo.hackernews.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.angelomarzo.hackernews.R
import com.angelomarzo.hackernews.ui.utils.TopBar
import kotlinx.coroutines.flow.collectLatest
import androidx.core.net.toUri

@Composable
fun HackerNewsApp(
    modifier: Modifier = Modifier,
) {
    val viewModel: HackerNewsViewModel = viewModel(factory = HackerNewsViewModel.Factory)
    val storyType = viewModel.storyType.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is HackerNewsEvent.OpenUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, event.url.toUri())
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("HackerNewsScreen", "No application found to handle URL: ${event.url}", e)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
        TopBar(
            R.string.app_name,
        )
    }
    ) { innerPadding ->
        HackerNewsScreen(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldContentPadding = innerPadding,
            lazyStoryItems = viewModel.stories.collectAsLazyPagingItems(),
            storyType = storyType.value,
            onStoryTypeSelected = {
                viewModel.selectStoryType(it)
            },
            onStorySelected = {story ->
                viewModel.onStoryClick(story)
            }
        )
    }
}
package com.angelomarzo.hackernews.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.angelomarzo.hackernews.R
import com.angelomarzo.hackernews.data.model.Story
import androidx.core.net.toUri

@Composable
fun HackerNewsScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val viewModel: HackerNewsViewModel = viewModel(factory = HackerNewsViewModel.Factory)
    val lazyStoryItems = viewModel.stories.collectAsLazyPagingItems()
    val storyType by viewModel.storyType.collectAsState()

    val isInitialLoad = lazyStoryItems.itemCount == 0 && lazyStoryItems.loadState.refresh is LoadState.Loading
    val isRefreshingWithContent = lazyStoryItems.itemCount > 0 && lazyStoryItems.loadState.refresh is LoadState.Loading


    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        StoryTypeSelector(
            selectedType = storyType,
            onTypeSelected = { viewModel.selectStoryType(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        PullToRefreshBox(
            isRefreshing = isRefreshingWithContent,
            onRefresh = lazyStoryItems::refresh,
            modifier = modifier
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isInitialLoad -> {
                        FullScreenLoading()
                    }

                    lazyStoryItems.loadState.refresh is LoadState.Error && lazyStoryItems.itemCount == 0 -> {
                        val error = (lazyStoryItems.loadState.refresh as LoadState.Error).error
                        FullScreenError(error = error) {
                            lazyStoryItems.retry()
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(
                                count = lazyStoryItems.itemCount,
                                key = lazyStoryItems.itemKey { it.id }
                            ) { index ->
                                lazyStoryItems[index]?.let { story ->
                                    StoryItem(story = story) { clickedStory ->
                                        clickedStory.url?.let { url ->
                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: ActivityNotFoundException) {
                                                Log.e("HackerNewsScreen", "No application found to handle URL: $url", e)

                                            }
                                        }
                                    }
                                }
                            }

                            when (lazyStoryItems.loadState.append) {
                                is LoadState.Loading -> {
                                    item { LoadingIndicator() }
                                }

                                is LoadState.Error -> {
                                    val error =
                                        (lazyStoryItems.loadState.append as LoadState.Error).error
                                    item {
                                        ErrorMessage(error = error) {
                                            lazyStoryItems.retry()
                                        }
                                    }
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryTypeSelector(
    selectedType: StoryType,
    onTypeSelected: (StoryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        StoryType.entries.forEach { type ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = type.ordinal,
                    count = StoryType.entries.size
                ),
                onClick = { onTypeSelected(type) },
                selected = type == selectedType
            ) {
                Text(type.name.replaceFirstChar { it.titlecase() })
            }
        }
    }
}

@Composable
fun StoryItem(
    story: Story,
    modifier: Modifier = Modifier,
    onClick: (Story) -> Unit,
) {
    Card(
        modifier = modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .clickable { onClick(story) }
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = story.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoChip(icon = painterResource(R.drawable.person_icon), text = story.by ?: "Unknown")
                InfoChip(icon = painterResource(R.drawable.score_icon), text = story.score?.toString() ?: "0")
                InfoChip(icon = painterResource(R.drawable.comment_icon), text = story.descendants?.toString() ?: "0")
            }
        }
    }
}

@Composable
fun InfoChip(icon: Painter, text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = null, // L'icona è decorativa
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FullScreenError(error: Throwable, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: ${error.localizedMessage}", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun ErrorMessage(error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Couldn't load more items: ${error.localizedMessage}", color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}
package com.angelomarzo.hackernews.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.angelomarzo.hackernews.R
import com.angelomarzo.hackernews.data.model.Story
import kotlinx.coroutines.flow.flowOf
import java.time.Instant

@Composable
fun HackerNewsScreen(
    modifier: Modifier = Modifier,
    scaffoldContentPadding: PaddingValues = PaddingValues(dimensionResource(R.dimen.dimen_padding_zero)),
    lazyStoryItems: LazyPagingItems<Story>,
    storyType: StoryType,
    onStoryTypeSelected: (StoryType) -> Unit,
    onStorySelected: (Story) -> Unit,
) {

    val isInitialLoad = lazyStoryItems.itemCount == 0 && lazyStoryItems.loadState.refresh is LoadState.Loading
    val isRefreshingWithContent = lazyStoryItems.itemCount > 0 && lazyStoryItems.loadState.refresh is LoadState.Loading


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = scaffoldContentPadding.calculateTopPadding(),
                start = scaffoldContentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = scaffoldContentPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
    ) {
        StoryTypeSelector(
            selectedType = storyType,
            onTypeSelected = { onStoryTypeSelected(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.dimen_padding_medium))
        )

        PullToRefreshBox(
            isRefreshing = isRefreshingWithContent,
            onRefresh = lazyStoryItems::refresh,
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
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = dimensionResource(R.dimen.dimen_padding_small)),
                        ) {
                            items(
                                count = lazyStoryItems.itemCount,
                                key = lazyStoryItems.itemKey { it.id }
                            ) { index ->
                                lazyStoryItems[index]?.let { story ->
                                    StoryItem(
                                        story = story,
                                        modifier = Modifier.padding()
                                    ) { clickedStory ->
                                        onStorySelected(clickedStory)
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

@Preview
@Composable
fun HackerNewsScreenPreview() {
    val stories = List(10) { index ->
        Story(
            id = index,
            by = "author$index",
            time = Instant.now(),
            title = "Story Title $index",
            url = "http://example.com/$index",
            score = index * 10,
            descendants = index * 5,
            text = "This is the story text for story $index."
        )
    }
    val lazyPagingItems = flowOf(androidx.paging.PagingData.from(stories)).collectAsLazyPagingItems()

    MaterialTheme {
        HackerNewsScreen(
            lazyStoryItems = lazyPagingItems,
            storyType = StoryType.TOP,
            onStoryTypeSelected = {},
            onStorySelected = {}
        )
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

@Preview
@Composable
fun StoryTypeSelectorPreview() {
    StoryTypeSelector(
        selectedType = StoryType.TOP,
        onTypeSelected = {}
    )
}

@Composable
fun StoryItem(
    story: Story,
    modifier: Modifier = Modifier,
    onClick: (Story) -> Unit,
) {
    Card(
        modifier = modifier
            .padding(vertical = dimensionResource(R.dimen.dimen_story_item_vertical_padding))
            .fillMaxWidth()
            .clickable { onClick(story) }
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.dimen_card_elevation))
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.dimen_padding_medium))) {
            Text(
                text = story.title ?: stringResource(R.string.story_no_title),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dimen_padding_small)))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dimen_padding_medium)),
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoChip(icon = painterResource(R.drawable.person_icon), text = story.by ?: stringResource(R.string.story_author_unknown))
                InfoChip(icon = painterResource(R.drawable.score_icon), text = story.score?.toString() ?: stringResource(R.string.story_default_count))
                InfoChip(icon = painterResource(R.drawable.comment_icon), text = story.descendants?.toString() ?: stringResource(R.string.story_default_count))
            }
        }
    }
}

@Preview
@Composable
fun StoryItemPreview() {
    val story = Story(
        id = 1,
        by = "John Doe",
        time = Instant.now(),
        title = "This is a very long story title that should be truncated",
        url = "https://example.com",
        score = 100,
        descendants = 42,
        text = "Story text"
    )
    StoryItem(story = story, onClick = {})
}

@Composable
fun InfoChip(icon: Painter, text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dimen_spaced_by_small)),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = null, // L'icona è decorativa
            modifier = Modifier.size(dimensionResource(R.dimen.dimen_icon_size_small)),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun InfoChipPreview() {
    InfoChip(
        icon = painterResource(id = R.drawable.person_icon),
        text = "John Doe"
    )
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

@Preview
@Composable
fun FullScreenLoadingPreview() {
    FullScreenLoading()
}

@Composable
fun LoadingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.dimen_padding_medium)),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun LoadingIndicatorPreview() {
    LoadingIndicator()
}

@Composable
fun FullScreenError(error: Throwable, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.error_prefix, error.localizedMessage), color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dimen_padding_small)))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Preview
@Composable
fun FullScreenErrorPreview() {
    FullScreenError(
        error = Exception("Something went wrong"),
        onRetry = {}
    )
}

@Composable
fun ErrorMessage(error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.dimen_padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.error_load_more_prefix, error.localizedMessage), color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.dimen_padding_small)))
        OutlinedButton(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Preview
@Composable
fun ErrorMessagePreview() {
    ErrorMessage(
        error = Exception("Couldn't load more items"),
        onRetry = {}
    )
}
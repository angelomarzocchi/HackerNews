package com.angelomarzo.hackernews.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.angelomarzo.hackernews.data.model.Story

@Composable
fun HackerNewsScreen(viewModel: HackerNewsViewModel) {
    val lazyStoryItems = viewModel.stories.collectAsLazyPagingItems()
    val currentStoryType = viewModel.storyType.collectAsState()

    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.selectStoryType(StoryType.TOP) },
                colors = if (currentStoryType.value == StoryType.TOP) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()
            ) { Text("Top") }

            Button(
                onClick = { viewModel.selectStoryType(StoryType.NEW) },
                colors = if (currentStoryType.value == StoryType.NEW) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()
            ) { Text("New") }

            Button(
                onClick = { viewModel.selectStoryType(StoryType.BEST) },
                colors = if (currentStoryType.value == StoryType.BEST) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()
            ) { Text("Best") }
        }


        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(count = lazyStoryItems.itemCount) { index ->
                // ...
                StoryItem(lazyStoryItems[index])
            }
        }
    }
}

@Composable
fun StoryItem(story: Story?) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = story?.title ?: "No Title", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(text = "by ${story?.by}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
            // Aggiungi altri dettagli se vuoi
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(error: Throwable) {
    Text("Error: ${error.message}", modifier = Modifier.padding(16.dp))
}
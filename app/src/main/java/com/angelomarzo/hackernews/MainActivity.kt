package com.angelomarzo.hackernews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.angelomarzo.hackernews.ui.HackerNewsScreen
import com.angelomarzo.hackernews.ui.HackerNewsViewModel
import com.angelomarzo.hackernews.ui.theme.HackerNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HackerNewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HackerNewsScreen(
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}

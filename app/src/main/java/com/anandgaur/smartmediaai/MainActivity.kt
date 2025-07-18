package com.anandgaur.smartmediaai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.anandgaur.smartmediaai.screen.VideoSummarizationScreen
import com.anandgaur.smartmediaai.ui.theme.SmartMediaAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint //  Required for Hilt ViewModel injection
class MainActivity : ComponentActivity() {
    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartMediaAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoSummarizationScreen() // no need to pass context if not required
                }
            }
        }
    }
}

package com.anandgaur.smartmediaai.screen

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.anandgaur.smartmediaai.R
import com.anandgaur.smartmediaai.player.VideoPlayer
import com.anandgaur.smartmediaai.player.VideoSelectionDropdown
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.anandgaur.smartmediaai.ui.OutputTextDisplay
import com.anandgaur.smartmediaai.ui.TextToSpeechControls
import com.anandgaur.smartmediaai.ui.theme.PrimaryBlue
import com.anandgaur.smartmediaai.util.sampleVideoList
import com.anandgaur.smartmediaai.viewmodel.OutputTextState
import com.anandgaur.smartmediaai.viewmodel.VideoSummarizationViewModel
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import java.util.Locale

/**
 * Composable function for the AI Video Summarization screen.
 *
 * This screen allows users to select a video, play it, and generate a summary of its content
 * using Firebase AI. It also provides text-to-speech functionality to read out
 */
@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSummarizationScreen(viewModel: VideoSummarizationViewModel = hiltViewModel()) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(sampleVideoList.first().uri) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val outputTextState by viewModel.outputText.collectAsState()
    val showListenButton = outputTextState is OutputTextState.Success
    var textForSpeech by remember { mutableStateOf("") }
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var isInitialized by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    val videoOptions = sampleVideoList
    val context = LocalContext.current

    var youtubePlayer: YouTubePlayer? by remember { mutableStateOf(null) }
    var currentYouTubeVideoId: String? by remember { mutableStateOf(null) }
    var showYoutubeLoading by remember { mutableStateOf(false) }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(selectedVideoUri) {
        selectedVideoUri?.takeIf { it.toString().isNotEmpty() }?.let { uri ->
            val videoUrl = uri.toString()
            if (videoUrl.contains("youtu.be") || videoUrl.contains("youtube.com")) {
                val videoId = extractYouTubeVideoId(videoUrl)
                if (videoId != null) {
                    currentYouTubeVideoId = videoId
                    youtubePlayer?.loadVideo(videoId, 0f)
                } else {
                    Log.e("VideoPlayer", "Could not extract YouTube video ID from URL: $videoUrl")
                }
                exoPlayer.stop()
            } else {
                currentYouTubeVideoId = null
                onSelectedVideoChange(
                    context,
                    selectedVideoUri,
                    exoPlayer,
                    textToSpeech,
                    onSpeakingStateChange = { speaking, paused ->
                        isSpeaking = speaking
                        isPaused = paused
                    },
                )
            }
        }
    }

    DisposableEffect(true) {
        textToSpeech = initializeTextToSpeech(context) { initialized ->
            isInitialized = initialized
        }
        onDispose {
            textToSpeech?.shutdown()
        }
    }

    var selectedAccent by remember { mutableStateOf(Locale.FRANCE) }
    val accentOptions = listOf(
        Locale.UK, Locale.FRANCE, Locale.GERMANY,
        Locale.ITALY, Locale.JAPAN, Locale.KOREA, Locale.US,
    )
    var isAccentDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(text = stringResource(R.string.video_summarization_title))
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                VideoSelectionDropdown(
                    selectedVideoUri = selectedVideoUri,
                    isDropdownExpanded = isDropdownExpanded,
                    videoOptions = videoOptions,
                    onVideoUriSelected = { uri ->
                        selectedVideoUri = uri
                        viewModel.clearOutputText()
                    },
                    onDropdownExpanded = { expanded ->
                        isDropdownExpanded = expanded
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (currentYouTubeVideoId != null) {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                YouTubePlayerView(context).apply {
                                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                            youtubePlayer = youTubePlayer
                                            currentYouTubeVideoId?.let { youTubePlayer.loadVideo(it, 0f) }
                                        }

                                        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                                            showYoutubeLoading = state == PlayerConstants.PlayerState.BUFFERING
                                        }
                                    })
                                }
                            }
                        )
                        if (showYoutubeLoading) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                } else {
                    VideoPlayer(exoPlayer = exoPlayer, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.getVideoSummary(selectedVideoUri!!)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(stringResource(R.string.summarize_video_button), color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show loader while generating summary
                if (outputTextState is OutputTextState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (showListenButton && outputTextState is OutputTextState.Success) {
                    textForSpeech = (outputTextState as OutputTextState.Success).text

                    Spacer(modifier = Modifier.height(8.dp))

                    TextToSpeechControls(
                        isInitialized = isInitialized,
                        isSpeaking = isSpeaking,
                        isPaused = isPaused,
                        textToSpeech = textToSpeech,
                        speechText = textForSpeech,
                        selectedAccent = selectedAccent,
                        accentOptions = accentOptions,
                        onSpeakingStateChange = { speaking, paused ->
                            isSpeaking = speaking
                            isPaused = paused
                        },
                        isAccentDropdownExpanded = isAccentDropdownExpanded,
                        onAccentSelected = { accent ->
                            selectedAccent = accent
                        },
                        onAccentDropdownExpanded = { expanded ->
                            isAccentDropdownExpanded = expanded
                        },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutputTextDisplay(outputTextState, modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    DisposableEffect(youtubePlayer) {
        onDispose { /* No release needed for YouTubePlayerView */ }
    }
}


@UnstableApi
suspend fun onSelectedVideoChange(
    context: Context,
    selectedVideoUri: Uri?,
    exoPlayer: ExoPlayer,
    textToSpeech: TextToSpeech?,
    onSpeakingStateChange: (speaking: Boolean, paused: Boolean) -> Unit,
) {
    selectedVideoUri?.takeIf { it.toString().isNotEmpty() }?.let { uri ->
        val videoUrl = uri.toString()
        Log.d("VideoPlayer", "onSelectedVideoChange: Processing video URL: $videoUrl")
        if (videoUrl.contains("youtu.be") || videoUrl.contains("youtube.com")) {
            val videoId = extractYouTubeVideoId(videoUrl)
            if (videoId == null) {
                Log.e("VideoPlayer", "Could not extract YouTube video ID from URL: $videoUrl")
                return
            }

            try {
                val youtubeJExtractor = YoutubeJExtractor()
                val videoPlayerConfig: VideoPlayerConfig = withContext(Dispatchers.IO) {
                    youtubeJExtractor.extract(videoId)
                }

                var extractedUrl = videoPlayerConfig.streamingData?.adaptiveVideoStreams
                    ?.sortedByDescending { it.bitrate }
                    ?.firstOrNull { it.url != null && it.url.isNotEmpty() }?.url

                if (extractedUrl.isNullOrEmpty()) {
                    // If adaptive streams are not found, try muxed streams
                    val muxedStream = videoPlayerConfig.streamingData?.muxedStreams
                        ?.sortedByDescending { it.bitrate }
                        ?.firstOrNull { it.url != null && it.url.isNotEmpty() }

                    if (muxedStream != null) {
                        extractedUrl = muxedStream.url
                    } else if (videoPlayerConfig.videoDetails?.isLiveContent == true) {
                        // For live content, try DASH or HLS manifests
                        extractedUrl = videoPlayerConfig.streamingData?.dashManifestUrl
                            ?: videoPlayerConfig.streamingData?.hlsManifestUrl
                    }
                }

                if (!extractedUrl.isNullOrEmpty()) {
                    Log.d("VideoPlayer", "Extracted playable URL: $extractedUrl")
                    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    val defaultDataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
                    val mediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(extractedUrl.toUri()))
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                } else {
                    Log.e("VideoPlayer", "No suitable video URL found after extraction attempt.")
                }
            } catch (e: ExtractionException) {
                Log.e("VideoPlayer", "ExtractionException: ${e.message}", e)
            } catch (e: YoutubeRequestException) {
                Log.e("VideoPlayer", "YoutubeRequestException: ${e.message}", e)
            } catch (e: Exception) {
                Log.e("VideoPlayer", "Error extracting YouTube URL: ${e.message}", e)
            }
        } else {
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
        }
        textToSpeech?.stop()
        onSpeakingStateChange(false, true)
    }
}

fun extractYouTubeVideoId(youtubeUrl: String): String? {
    val youtubeRegex = "(?:https?:\\/\\/(?:www\\.)?|www\\.)(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=))([\\w-]{11})(?:\\S+)?".toRegex()
    val matchResult = youtubeRegex.find(youtubeUrl)
    return matchResult?.groups?.get(1)?.value
}



fun initializeTextToSpeech(context: Context, onInitialized: (Boolean) -> Unit): TextToSpeech {
    val textToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            onInitialized(true)
        } else {
            Log.e("TextToSpeech", "Initialization failed")
            onInitialized(false)
        }
    }
    return textToSpeech
}

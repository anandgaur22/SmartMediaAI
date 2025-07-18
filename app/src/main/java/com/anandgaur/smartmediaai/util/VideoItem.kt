package com.anandgaur.smartmediaai.util

import android.net.Uri
import androidx.core.net.toUri
import com.anandgaur.smartmediaai.R

/**
 * Data class to represent a video item with a title and URI.
 */
data class VideoItem(
    val titleResId: Int,
    val uri: Uri,
)

val sampleVideoList = listOf(
    VideoItem(
        R.string.video_title_big_buck_bunny,
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4".toUri(),
    ),
    VideoItem(
        R.string.video_title_android_spotlight_shorts,
        "https://storage.googleapis.com/exoplayer-test-media-0/shorts_android_developers/shorts_10.mp4".toUri(),
    ),
    VideoItem(
        R.string.video_title_youtube,
        "https://www.youtube.com/watch?v=y7ImjmLFEPI".toUri(),
    ),
    VideoItem(
        R.string.video_title_tears_of_steel,
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4".toUri(),
    ),
    VideoItem(
        R.string.video_title_for_bigger_blazes,
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4".toUri(),
    ),
    VideoItem(
        R.string.video_title_for_bigger_escape,
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4".toUri(),
    ),
)
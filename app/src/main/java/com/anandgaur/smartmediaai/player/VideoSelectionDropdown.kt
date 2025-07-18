package com.anandgaur.smartmediaai.player

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.anandgaur.smartmediaai.R
import com.anandgaur.smartmediaai.util.VideoItem

/**
 * A composable function that displays a dropdown menu for selecting a video from a list of options.
 */
@Composable
fun VideoSelectionDropdown(
    selectedVideoUri: Uri?,
    isDropdownExpanded: Boolean,
    videoOptions: List<VideoItem>,
    onVideoUriSelected: (Uri) -> Unit,
    onDropdownExpanded: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {

        // OutlinedTextField (non-clickable)
        OutlinedTextField(
            value = selectedVideoUri?.let {
                videoOptions.firstOrNull { videoItem -> videoItem.uri == selectedVideoUri }
                    ?.let { stringResource(it.titleResId) }
            } ?: stringResource(R.string.select_video_placeholder),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = stringResource(R.string.dropdown_content_description),
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
        )

        // Transparent clickable Box overlay to make the whole field clickable
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onDropdownExpanded(!isDropdownExpanded) }
        )

        // Dropdown menu
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { onDropdownExpanded(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            videoOptions.forEach { videoItem ->
                DropdownMenuItem(
                    text = { Text(stringResource(videoItem.titleResId)) },
                    onClick = {
                        onVideoUriSelected(videoItem.uri)
                        onDropdownExpanded(false)
                    }
                )
            }
        }
    }
}

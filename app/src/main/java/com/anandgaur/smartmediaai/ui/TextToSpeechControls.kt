package com.anandgaur.smartmediaai.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.anandgaur.smartmediaai.R
import com.anandgaur.smartmediaai.ui.theme.PrimaryBlue
import java.util.Locale

/**
 * Composable function that provides controls for Text-to-Speech functionality.
 *
 * This function displays a UI that allows the user to:
 * - Select a language accent for the Text-to-Speech engine.
 * - Initiate speech synthesis for the provided text.
 * - Pause the ongoing speech.
 */
@Composable
fun TextToSpeechControls(
    isInitialized: Boolean,
    isSpeaking: Boolean,
    isPaused: Boolean,
    textToSpeech: TextToSpeech?,
    speechText: String,
    selectedAccent: Locale,
    accentOptions: List<Locale>,
    onSpeakingStateChange: (speaking: Boolean, paused: Boolean) -> Unit,
    isAccentDropdownExpanded: Boolean,
    onAccentSelected: (Locale) -> Unit,
    onAccentDropdownExpanded: (Boolean) -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = selectedAccent.displayLanguage,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { onAccentDropdownExpanded(!isAccentDropdownExpanded) },
                )
            },
            modifier = Modifier
                .clickable { onAccentDropdownExpanded(!isAccentDropdownExpanded) }
                .padding(end = 8.dp)
                .weight(1f),
        )
        DropdownMenu(
            expanded = isAccentDropdownExpanded,
            onDismissRequest = { onAccentDropdownExpanded(false) },
        ) {
            accentOptions.forEach { accent ->
                DropdownMenuItem(text = { Text(accent.displayLanguage) }, onClick = {
                    onAccentSelected(accent)
                    onAccentDropdownExpanded(false)
                })
            }
        }

        if (isInitialized && !isSpeaking) {
            Button(
                onClick = {
                    handleSpeakButtonClick(
                        textToSpeech, speechText, selectedAccent, onSpeakingStateChange,
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(text = stringResource(R.string.text_listen_to_ai_output), color = Color.White)
            }
        }

        if (isSpeaking && !isPaused) {
            Button(
                onClick = {
                    textToSpeech?.stop()
                    onSpeakingStateChange(false, true)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(text = stringResource(R.string.pause), color = Color.White)
            }
        }
    }
}

private fun handleSpeakButtonClick(
    textToSpeech: TextToSpeech?,
    textForSpeech: String,
    selectedAccent: Locale,
    onSpeakingStateChange: (speaking: Boolean, paused: Boolean) -> Unit,
) {
    // Check if the voice and language is supported
    val result = textToSpeech?.voice?.locale?.let {
        textToSpeech.setLanguage(selectedAccent)
    }
    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        Log.e("TextToSpeech", "Language not supported")
    } else {
        // Start speaking
        textToSpeech?.speak(
            textForSpeech, TextToSpeech.QUEUE_FLUSH, null, null,
        )
        onSpeakingStateChange(true, false)
    }
}
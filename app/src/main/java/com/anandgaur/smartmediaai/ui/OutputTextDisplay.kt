package com.anandgaur.smartmediaai.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import com.anandgaur.smartmediaai.R
import com.anandgaur.smartmediaai.viewmodel.OutputTextState

/**
 * Composable function that displays text based on the state of the `outputText`.
 *
 * This function renders different text content depending on the `OutputTextState` provided.
 * It handles four states:
 * - `Initial`: Displays a placeholder message indicating that text is being generated.
 * - `Loading`: Shows a loading message to indicate that text generation is in progress.
 * - `Success`: Renders the generated text, providing a styled display within a scrollable container.
 * - `Error`: Displays an error message along with the specific error encountered during text generation.
 */
@Composable
fun OutputTextDisplay(outputText: OutputTextState, modifier: Modifier = Modifier) {

    when (outputText) {
        is OutputTextState.Initial -> {
            Text(
                text = stringResource(
                    R.string.output_text_combined,
                    stringResource(R.string.output_text_generated_placeholder),
                    stringResource(R.string.output_text_initial),
                ),
                fontStyle = FontStyle.Italic, modifier = Modifier.fillMaxWidth(),
            )
        }

        is OutputTextState.Loading -> {
            Text(
                text = stringResource(
                    R.string.output_text_combined,
                    stringResource(R.string.output_text_generated_placeholder),
                    stringResource(R.string.output_text_loading),
                ),
                fontStyle = FontStyle.Italic, modifier = Modifier.fillMaxWidth(),
            )
        }

        is OutputTextState.Success -> {
            Text(
                text = stringResource(
                    R.string.output_text_combined,
                    stringResource(R.string.output_text_generated_placeholder),
                    outputText.text,
                ),
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        is OutputTextState.Error -> {
            Text(
                text = stringResource(
                    R.string.output_text_combined,
                    stringResource(R.string.output_text_generated_placeholder),
                    outputText.errorMessage,
                ),
                fontStyle = FontStyle.Italic, modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
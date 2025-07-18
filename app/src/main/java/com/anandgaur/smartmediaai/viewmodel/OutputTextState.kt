package com.anandgaur.smartmediaai.viewmodel

sealed class OutputTextState {
    data object Initial : OutputTextState()
    data class Success(val text: String) : OutputTextState()
    data class Error(val errorMessage: String) : OutputTextState()
    data object Loading : OutputTextState()
}
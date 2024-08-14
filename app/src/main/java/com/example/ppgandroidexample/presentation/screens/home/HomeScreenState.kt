package com.example.ppgandroidexample.presentation.screens.home

import androidx.compose.ui.graphics.Color

data class HomeScreenState(
    val error: String? = null,
    val subId: String? = null,
    val isSubscriber: Boolean? = false,
    val message: String? = null,
    val messageColor: Color = Color.Transparent,
    val isLoading: Boolean = false
)

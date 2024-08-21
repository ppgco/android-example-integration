package com.example.ppgandroidexample.presentation.screens.transactional

import androidx.compose.ui.graphics.Color

data class TransactionalScreenState(
    val error: String? = null,
    val subId: String? = null,
    val externalId: String = "",
    val message: String? = null,
    val messageColor: Color = Color.Transparent,
    val isLoading: Boolean = false
)

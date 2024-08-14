package com.example.ppgandroidexample.presentation.common_components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MessageSnackbar(message: String?, width: Float = 0.9f, msgColor: Color) {
    if (!message.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(width)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Snackbar(
                contentColor = msgColor,
                containerColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, msgColor)
            ) {
                Text(
                    text = message,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

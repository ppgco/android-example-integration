package com.example.ppgandroidexample.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ppgandroidexample.presentation.common_components.MessageSnackbar
import com.example.ppgandroidexample.presentation.common_components.SendBeaconCard

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val homeScreenState = viewModel.state.value
    val errorMsg = homeScreenState.error
    val successMessage = homeScreenState.message
    val context = LocalContext.current
    var showSendBeaconCard by remember { mutableStateOf(false) }
    var clickedButton by remember { mutableStateOf<String?>(null) }

    val buttons = listOf(
        Pair("Register") { viewModel.registerSubscriber(context) },
        Pair("Unregister") { viewModel.unregisterSubscriber() },
        Pair("Subscriber ID") { viewModel.getSubscriberId() },
        Pair("Is Subscribed") { viewModel.isSubscriberActive() },
        Pair("Send Beacon") { showSendBeaconCard = true },
        Pair("Send Push Notification") { viewModel.sendTransactionalPush() }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            buttons.chunked(2).forEach { rowButtons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    rowButtons.forEach { (buttonText, onClickAction) ->
                        ShinyButton(
                            text = buttonText,
                            isSelected = clickedButton == buttonText,
                            onClick = {
                                clickedButton = buttonText
                                onClickAction()
                            },
                            modifier = Modifier
                                .size(150.dp)
                                .padding(8.dp)
                        )
                    }
                    if (rowButtons.size < 2) {
                        Spacer(
                            modifier = Modifier
                                .size(150.dp)
                                .padding(8.dp)
                        )
                    }
                }
            }

            if (!successMessage.isNullOrBlank() || !errorMsg.isNullOrBlank()) {
                MessageSnackbar(
                    message = successMessage ?: errorMsg,
                    0.5f,
                    msgColor = homeScreenState.messageColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        if (showSendBeaconCard) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SendBeaconCard(
                    onSendClick = { tag, label, ttl ->
                        viewModel.sendBeacon(tag, label, ttl)
                        showSendBeaconCard = false
                    },
                    onCancelClick = {
                        showSendBeaconCard = false
                    }
                )
            }
        }
    }
}

@Composable
fun ShinyButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateFloatAsState(
        targetValue = if (isSelected) 0.5f else 1f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Gray.copy(alpha = backgroundColor)
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
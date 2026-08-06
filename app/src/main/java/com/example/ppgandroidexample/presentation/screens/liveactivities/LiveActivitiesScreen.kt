package com.example.ppgandroidexample.presentation.screens.liveactivities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ppgandroidexample.data.liveactivity.LiveActivityDemoPayloads
import com.example.ppgandroidexample.presentation.common_components.MessageSnackbar
import com.example.ppgandroidexample.presentation.navigation.Screens

/**
 * Live Activities (Android 16 "Live Updates") demo screen.
 *
 * Two ways of driving a live notification are shown:
 *  1. **Subscription** - `subscribeToLiveActivity()` registers the device on a
 *     backend live notification campaign; the backend then pushes the updates.
 *  2. **Local simulation** - `simulateLiveActivityPush()` feeds the SDK the very
 *     same envelope an FCM data message would carry, so the notification can be
 *     rendered without any campaign configured in PPG.
 *
 * Rendering requires Android 16 (API 36+). On older devices the SDK ignores the
 * pushes, and the screen says so.
 */
@Composable
fun LiveActivitiesScreen(
    navController: NavController,
    viewModel: LiveActivitiesScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    // Alternative: If you don't use InAppMessageHelper.setupWithNavController() in NavGraph,
    // you can trigger in-app messages manually on each screen using LaunchedEffect:
    //
    // LaunchedEffect(Screens.LiveActivities.route) {
    //     InAppMessagesSDK.getInstance().showActiveMessages(Screens.LiveActivities.route)
    // }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // The activity is edge-to-edge and this screen scrolls from the
                // very top, so keep the content clear of the system bars
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Live Activities",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = if (state.isSupported) {
                    "Supported on this device (Android 16 / API 36+)"
                } else {
                    "Not supported - Live Activities need Android 16 (API 36+). " +
                        "Calls stay safe, but nothing is rendered."
                },
                fontSize = 14.sp,
                color = if (state.isSupported) Color.Green else Color.Red,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Subscription")

            Text(
                text = "Registers this device on a live notification campaign. " +
                    "Register a subscriber on the SDK section screen first.",
                fontSize = 13.sp,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.liveNotificationId,
                onValueChange = viewModel::onLiveNotificationIdChange,
                label = { Text("Live notification ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LiveActivityButton(
                    text = "Subscribe",
                    onClick = viewModel::subscribe,
                    modifier = Modifier.weight(1f)
                )
                LiveActivityButton(
                    text = "Unsubscribe",
                    onClick = viewModel::unsubscribe,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (state.laSubscriberId.isBlank()) {
                    "LA subscriber ID: not subscribed"
                } else {
                    "LA subscriber ID: ${state.laSubscriberId}"
                },
                fontSize = 13.sp,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Local simulation")

            Text(
                text = "Drives the match through simulateLiveActivityPush() - " +
                    "no backend campaign needed. Start the match, then score goals " +
                    "and move through the phases.",
                fontSize = 13.sp,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${LiveActivityDemoPayloads.HOME_TEAM} ${state.homeScore} : " +
                    "${state.awayScore} ${LiveActivityDemoPayloads.AWAY_TEAM}   " +
                    "(${state.currentPhase})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            val simulationButtons = listOf(
                "Start match" to viewModel::startMatch,
                "Home goal" to viewModel::homeGoal,
                "Away goal" to viewModel::awayGoal,
                "Next phase" to viewModel::nextPhase,
                "Hot message" to viewModel::sendHotMessage,
                "End match" to viewModel::endMatch
            )

            simulationButtons.chunked(2).forEach { rowButtons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowButtons.forEach { (text, onClick) ->
                        LiveActivityButton(
                            text = text,
                            onClick = onClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Tracked by the SDK")

            Text(
                text = state.activeActivities,
                fontSize = 13.sp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            LiveActivityButton(
                text = "Refresh status",
                onClick = viewModel::refreshStatus,
                modifier = Modifier.fillMaxWidth()
            )

            if (!state.message.isNullOrBlank() || !state.error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                MessageSnackbar(
                    message = state.message ?: state.error,
                    width = 0.95f,
                    msgColor = state.messageColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate(Screens.Home.route) },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "SDK section",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { navController.navigate(Screens.InAppMessages.route) },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "In-app messages",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun LiveActivityButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Cyan,
            textAlign = TextAlign.Center
        )
    }
}

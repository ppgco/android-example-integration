package com.example.ppgandroidexample.presentation.screens.inappmessages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ppgandroidexample.presentation.common_components.MessageSnackbar
import com.example.ppgandroidexample.presentation.navigation.Screens
import com.pushpushgo.inappmessages.InAppMessagesSDK

@Composable
fun InAppMessagesScreen(
    navController: NavController
) {
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var statusColor by remember { mutableStateOf(Color.Transparent) }
    var triggerKey by remember { mutableStateOf("") }
    var triggerValue by remember { mutableStateOf("") }

    // Alternative: If you don't use InAppMessageHelper.setupWithNavController() in NavGraph,
    // you can trigger in-app messages manually on each screen using LaunchedEffect:
    //
    // LaunchedEffect(Screens.InAppMessages.route) {
    //     InAppMessages.getInstance().showMessagesOnRoute(Screens.InAppMessages.route)
    // }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "In-App Messages",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    InAppMessagesSDK.getInstance().showActiveMessages(Screens.InAppMessages.route)
                    statusMessage = "Requested active messages for route: ${Screens.InAppMessages.route}"
                    statusColor = Color.Green
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Show Active Messages (Route)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Custom Trigger",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = triggerKey,
                onValueChange = { triggerKey = it },
                label = { Text("Trigger Key") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = triggerValue,
                onValueChange = { triggerValue = it },
                label = { Text("Trigger Value") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (triggerKey.isNotBlank()) {
                        InAppMessagesSDK.getInstance().showMessagesOnTrigger(
                            key = triggerKey,
                            value = triggerValue.ifBlank { triggerKey }
                        )
                        statusMessage = "Triggered: key='$triggerKey', value='$triggerValue'"
                        statusColor = Color.Green
                    } else {
                        statusMessage = "Trigger key cannot be empty"
                        statusColor = Color.Red
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Fire Custom Trigger",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    InAppMessagesSDK.getInstance().setJsActionHandler { jsCall ->
                        Log.d("InAppMessages", "JS Action received: $jsCall")
                        statusMessage = "JS Action: $jsCall"
                        statusColor = Color.Cyan
                    }
                    statusMessage = "JS Action Handler set"
                    statusColor = Color.Green
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Set JS Action Handler",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!statusMessage.isNullOrBlank()) {
                MessageSnackbar(
                    message = statusMessage,
                    width = 0.95f,
                    msgColor = statusColor
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    onClick = { navController.navigate(Screens.Transactional.route) },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "Transactional",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

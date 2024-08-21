package com.example.ppgandroidexample.presentation.screens.transactional

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ppgandroidexample.presentation.common_components.MessageSnackbar
import com.example.ppgandroidexample.presentation.common_components.ProvideExternalIdCard
import com.example.ppgandroidexample.presentation.navigation.Screens
import com.example.ppgandroidexample.presentation.screens.home.ShinyButton

@Composable
fun TransactionalScreen(
    navController: NavController,
    viewModel: TransactionalScreenViewModel = hiltViewModel()
) {

    val transactionalScreenState = viewModel.state.value
    val errorMsg = transactionalScreenState.error
    val successMessage = transactionalScreenState.message
    val externalIdState = transactionalScreenState.externalId
    var clickedButton by remember { mutableStateOf<String?>(null) }
    var showExternalIdCard by remember { mutableStateOf(false) }
    var actionToPerform: ((String) -> Unit)? by remember { mutableStateOf(null) }

    val buttons = listOf(
        Pair("Send push to subscriber ID") {
            viewModel.sendTransactionalPush(PushSentByLiterals.Id, null)
        },
        Pair("Send push to external ID") {
            actionToPerform = { externalId ->
                viewModel.sendTransactionalPush(PushSentByLiterals.ExternalId, externalId)
            }
            showExternalIdCard = true
        },
        Pair("Get Subs with given external ID") {
            actionToPerform = { externalId ->
                viewModel.getSubsWithExternalId(externalId)
            }
            showExternalIdCard = true
        },
        Pair("Assign external ID to your Subscriber") {
            actionToPerform = { externalId ->
                viewModel.assignSubToExternalId(externalId)
            }
            showExternalIdCard = true
        },
        Pair("Forget external ID from all Subs") {
            actionToPerform = { externalId ->
                viewModel.forgetExternalIdFromAllSubs(externalId)
            }
            showExternalIdCard = true
        },
        Pair("Unassign current external ID from your Subscriber") {
            viewModel.unassignSubFromExternalId()
        }
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
            Text(
                text = "Transactional API section",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(15.dp))

            if (!successMessage.isNullOrBlank() || !errorMsg.isNullOrBlank()) {
                MessageSnackbar(
                    message = successMessage ?: errorMsg,
                    0.9f,
                    msgColor = transactionalScreenState.messageColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Screens.Home.route) },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Nav to SDK section",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

        }

        if (showExternalIdCard) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ProvideExternalIdCard(
                    onSendClick = { externalId ->
                        actionToPerform?.invoke(externalId)
                        showExternalIdCard = false
                    },
                    onCancelClick = {
                        showExternalIdCard = false
                    }
                )
            }
        }
    }
}
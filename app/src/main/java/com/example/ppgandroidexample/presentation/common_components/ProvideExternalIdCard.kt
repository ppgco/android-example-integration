package com.example.ppgandroidexample.presentation.common_components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProvideExternalIdCard(
    onSendClick: (String) -> Unit,
    onCancelClick: () -> Unit
) {
    var externalId by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(300.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Provide External ID", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = externalId,
                onValueChange = { externalId = it },
                label = { Text("External ID") }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Row {
                Button(onClick = {
                    onSendClick(externalId)
                }) {
                    Text("Send")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onCancelClick) {
                    Text("Cancel")
                }
            }
        }
    }
}

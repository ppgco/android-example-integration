package com.example.ppgandroidexample.presentation.common_components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SendBeaconCard(
    onSendClick: (String, String, Int) -> Unit,
    onCancelClick: () -> Unit
) {
    var tag by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    var ttl by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .padding(16.dp)
            .size(400.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Send Beacon", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                label = { Text("Tag") }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label") }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ttl,
                onValueChange = { ttl = it },
                label = { Text("TTL") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    val ttlInt = ttl.toIntOrNull() ?: 0
                    onSendClick(tag, label, ttlInt)
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

package com.example.ppgandroidexample.presentation.screens.transactional

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun TransactionalScreen(
    navController: NavController,
    viewModel: TransactionalScreenViewModel = hiltViewModel()
) {

    val transactionalScreenState = viewModel.state
}
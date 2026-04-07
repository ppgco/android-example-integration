package com.example.ppgandroidexample.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.ppgandroidexample.presentation.screens.home.HomeScreen
import com.example.ppgandroidexample.presentation.screens.inappmessages.InAppMessagesScreen
import com.example.ppgandroidexample.presentation.screens.transactional.TransactionalScreen
import com.pushpushgo.inappmessages.utils.InAppMessageHelper

@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {
    // Automatically trigger in-app messages on every navigation destination change.
    // The SDK will call showMessagesOnRoute(route) for each destination.
    DisposableEffect(navController) {
        val listener = InAppMessageHelper.setupWithNavController(navController)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screens.Home.route
    ){
        composable(route = Screens.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screens.Transactional.route,
            deepLinks = listOf(navDeepLink { uriPattern = "app://www.example.com/transactional" })) {
            TransactionalScreen(navController = navController)
        }
        composable(route = Screens.InAppMessages.route) {
            InAppMessagesScreen(navController = navController)
        }
    }
}
package com.example.ppgandroidexample.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.example.ppgandroidexample.presentation.screens.home.HomeScreen
import com.example.ppgandroidexample.presentation.screens.transactional.TransactionalScreen

@Composable
fun SetUpNavGraph(
    navController: NavHostController
) {
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
    }
}
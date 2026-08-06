package com.example.ppgandroidexample.presentation.navigation

sealed class Screens(val route: String) {
    data object Home: Screens("home")
    data object Transactional: Screens("transactional")
    data object InAppMessages: Screens("in_app_messages")
    data object LiveActivities: Screens("live_activities")
}
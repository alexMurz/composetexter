package com.alexmurz.composetexter

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexmurz.composetexter.ui.TopicListScreen

private const val ROUTE_INBOX = "inbox"

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_INBOX,
    ) {
        composable(ROUTE_INBOX) {
            TopicListScreen()
        }
    }
}


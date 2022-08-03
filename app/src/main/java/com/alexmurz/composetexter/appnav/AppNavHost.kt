package com.alexmurz.composetexter.appnav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexmurz.composetexter.modules.topic.ui.TopicList

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inbox",
    ) {
        composable("inbox") {
            TopicList()
        }
    }
}


package com.alexmurz.composetexter

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alexmurz.composetexter.ui.FeedScreen
import com.alexmurz.composetexter.ui.TopicListScreen
import com.alexmurz.messages.model.MessageChainParent

private const val ROUTE_INBOX = "inbox"

private const val ROUTE_MESSAGE_FEED = "feed/{src}"
private fun NavHostController.navigateToMessageFeed(parent: MessageChainParent) {
    navigate("feed/${parent.id}")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_INBOX,
    ) {
        composable(
            route = ROUTE_INBOX,
        ) {
            TopicListScreen(
                openMessageChain = navController::navigateToMessageFeed
            )
        }

        composable(
            route = ROUTE_MESSAGE_FEED,
            arguments = listOf(
                navArgument("src") { type = NavType.LongType }
            ),
        ) { startEntry ->
            val parent = requireNotNull(
                startEntry.arguments?.getLong("src")?.let(MessageChainParent::of)
            ) {
                "Cannot open message feed without parent id"
            }
            FeedScreen(parent)
        }
    }
}


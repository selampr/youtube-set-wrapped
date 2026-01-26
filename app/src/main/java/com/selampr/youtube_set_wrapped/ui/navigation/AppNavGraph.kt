package com.selampr.youtube_set_wrapped.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.selampr.youtube_set_wrapped.ui.StatsViewModel
import com.selampr.youtube_set_wrapped.ui.TopVideoScreen
import com.selampr.youtube_set_wrapped.ui.UploadScreen
import com.selampr.youtube_set_wrapped.ui.WelcomeScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinue = { navController.navigate(Routes.UPLOAD) }
            )
        }

        composable(Routes.UPLOAD) { backStackEntry ->
            val rootEntry = androidx.compose.runtime.remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WELCOME)
            }
            val vm = hiltViewModel<StatsViewModel>(rootEntry)
            UploadScreen(
                vm = vm,
                onGenerate = { navController.navigate(Routes.TOP_VIDEO) }
            )
        }

        composable(Routes.TOP_VIDEO) { backStackEntry ->
            val rootEntry = androidx.compose.runtime.remember(backStackEntry) {
                navController.getBackStackEntry(Routes.WELCOME)
            }
            val vm = hiltViewModel<StatsViewModel>(rootEntry)
            TopVideoScreen(vm = vm)
        }

    }
}

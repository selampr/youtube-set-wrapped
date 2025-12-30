package com.selampr.youtube_set_wrapped.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.selampr.youtube_set_wrapped.ui.StatsViewModel
import com.selampr.youtube_set_wrapped.ui.UploadScreen
import com.selampr.youtube_set_wrapped.ui.WelcomeScreen
import com.selampr.youtube_set_wrapped.ui.screens.StatsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(Routes.UPLOAD)
                }
            )
        }

        composable(Routes.UPLOAD) { backStackEntry ->
            val vm = hiltViewModel<StatsViewModel>(backStackEntry)
            UploadScreen(vm = vm, onGenerate = { navController.navigate(Routes.STATS) })
        }

        composable(Routes.STATS) { backStackEntry ->
            // Share the same StatsViewModel instance used in the upload screen so the computed stats are preserved.
            val uploadEntry = remember(backStackEntry) { navController.getBackStackEntry(Routes.UPLOAD) }
            val vm = hiltViewModel<StatsViewModel>(uploadEntry)
            StatsScreen(vm = vm)
        }


    }
}

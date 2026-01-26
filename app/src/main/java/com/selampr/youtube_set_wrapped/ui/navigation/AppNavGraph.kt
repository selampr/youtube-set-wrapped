package com.selampr.youtube_set_wrapped.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.selampr.youtube_set_wrapped.ui.StatsViewModel
import com.selampr.youtube_set_wrapped.ui.TestVideoScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.TEST
    ) {

        composable(Routes.TEST) { backStackEntry ->
            val vm = hiltViewModel<StatsViewModel>(backStackEntry)
            TestVideoScreen(vm = vm)
        }

    }
}

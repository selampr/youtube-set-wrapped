package com.selampr.youtube_set_wrapped

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.selampr.youtube_set_wrapped.ui.navigation.AppNavGraph
import com.selampr.youtube_set_wrapped.ui.theme.YoutubeSetwrappedTheme
import com.selampr.youtube_set_wrapped.ui.theme.animations.LottieBackgroundCrop
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            YoutubeSetwrappedTheme {
                Box(Modifier.fillMaxSize()) {

                    LottieBackgroundCrop()

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )

                    AppNavGraph(navController)
                }
            }
        }
    }
}

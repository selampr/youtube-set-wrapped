package com.selampr.youtube_set_wrapped.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selampr.youtube_set_wrapped.ui.theme.ChelseaMarket
import com.selampr.youtube_set_wrapped.ui.theme.Inter

@Composable
fun AiVibeScreen(
    vm: StatsViewModel,
    onBack: () -> Unit
) {
    val vibe = vm.aiVibe
    val isLoading = vm.isAiVibeLoading
    val error = vm.aiError

    LaunchedEffect(Unit) {
        if (vibe == null && !isLoading) {
            vm.generateAiVibe()
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val alphaBlink by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .clickable { onBack() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "THIS YEAR FELT LIKE",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.1.sp
            )
        )

        Spacer(Modifier.height(10.dp))

        when {
            isLoading -> Text(
                text = "generating...",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
            error != null -> Text(
                text = error,
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
            vibe != null -> Text(
                text = vibe,
                style = TextStyle(
                    fontFamily = ChelseaMarket,
                    fontSize = 34.sp,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = Offset(2f, 2f),
                        blurRadius = 6f
                    )
                )
            )
            else -> Text(
                text = "Could not generate the line.",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "tap to go back",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = alphaBlink)
            )
        )
    }
}

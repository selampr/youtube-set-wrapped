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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.selampr.youtube_set_wrapped.ui.theme.ChelseaMarket
import com.selampr.youtube_set_wrapped.ui.theme.Inter

@Composable
fun TotalsScreen(
    vm: StatsViewModel,
    onContinue: () -> Unit
) {
    val isLoading = vm.isDurationLoading
    val totalMinutes = vm.totalDurationMinutes
    val error = vm.aiError
    val topThumbUrl = vm.getTopVideoThumbnailUrlFromStats()

    LaunchedEffect(Unit) {
        if (totalMinutes == null && !isLoading) {
            vm.generateTotalDurationMinutes()
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
            .clickable(enabled = !isLoading) { onContinue() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TOTAL WATCHED TIME",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.1.sp
            )
        )

        Spacer(Modifier.height(12.dp))

        when {
            isLoading -> Text(
                text = "calculating...",
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
            totalMinutes != null -> Text(
                text = "$totalMinutes MINUTES",
                style = TextStyle(
                    fontFamily = ChelseaMarket,
                    fontSize = 36.sp,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = Offset(2f, 2f),
                        blurRadius = 6f
                    )
                )
            )
            else -> Text(
                text = "Could not calculate time.",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )
        }

        if (topThumbUrl != null && !isLoading && error == null) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "TOP SET THUMBNAIL",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 1.0.sp
                )
            )
            Spacer(Modifier.height(10.dp))
            AsyncImage(
                model = topThumbUrl,
                contentDescription = "Top video thumbnail",
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        if (!isLoading) {
            Spacer(Modifier.height(28.dp))
            Text(
                text = "tap to continue",
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = alphaBlink)
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

package com.selampr.youtube_set_wrapped.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
fun TopVideoScreen(vm: StatsViewModel) {
    val isLoading = vm.isTopVideoLoading
    val error = vm.topVideoError
    val title = vm.topVideoTitle
    val artist = vm.topVideoArtist
    val minutes = vm.topVideoMinutes
    val thumbnailUrl = vm.topVideoThumbnailUrl
    val line = vm.topVideoLine
    val isOpenAiConfigured = vm.isOpenAiConfigured()

    LaunchedEffect(Unit) {
        if (!isLoading && title == null) {
            vm.loadTopVideo()
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
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TOP VIDEO",
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
                text = "loading video info...",
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
            else -> {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = "Video thumbnail",
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (title != null) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = ChelseaMarket,
                            fontSize = 28.sp,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.4f),
                                offset = Offset(2f, 2f),
                                blurRadius = 6f
                            )
                        )
                    )
                }

                if (artist != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = artist,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }

                if (minutes != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$minutes MINUTES",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }

                if (line != null) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = line,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }

                if (line == null && minutes != null && isOpenAiConfigured) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "generating line...",
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = alphaBlink)
                        )
                    )
                }
            }
        }
    }
}

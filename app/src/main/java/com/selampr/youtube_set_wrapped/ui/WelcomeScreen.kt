package com.selampr.youtube_set_wrapped.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selampr.youtube_set_wrapped.ui.theme.ChelseaMarket
import com.selampr.youtube_set_wrapped.ui.theme.Inter

@Composable
fun WelcomeScreen(
    onContinue: () -> Unit
) {
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
            .clickable { onContinue() }, // TAP EN TODA LA PANTALLA
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "WELCOME TO YOUR",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.2.sp
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "SET WRAPPED",
            style = TextStyle(
                fontFamily = ChelseaMarket,
                fontSize = 46.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = Offset(2f, 2f),
                    blurRadius = 6f
                )
            )
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "tap to start",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = alphaBlink)
            )
        )
    }
}

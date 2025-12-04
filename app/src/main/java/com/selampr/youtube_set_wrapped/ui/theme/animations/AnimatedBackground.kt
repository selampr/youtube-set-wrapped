package com.selampr.youtube_set_wrapped.ui.theme.animations


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun LottieBackgroundCrop() {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(com.selampr.youtube_set_wrapped.R.raw.background)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Tamaño real de la pantalla
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    // Calculamos escalado dinámico cuando tengamos tamaño y composición
    val scale = remember(screenSize, composition) {
        if (composition == null || screenSize == IntSize.Zero) {
            1f
        } else {
            val compWidth = composition!!.bounds.width().toFloat()
            val compHeight = composition!!.bounds.height().toFloat()

            val scaleX = screenSize.width / compWidth
            val scaleY = screenSize.height / compHeight

            // Usamos el MAYOR → ContentScale.Crop
            maxOf(scaleX, scaleY)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { screenSize = it.size }
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale * 0.7f
                        scaleY = scale * 0.7f
                        translationX = 50f
                        translationY = 0f
                    }
            )
        }
    }
}

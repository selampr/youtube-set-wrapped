package com.selampr.youtube_set_wrapped.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selampr.youtube_set_wrapped.ui.theme.ChelseaMarket
import com.selampr.youtube_set_wrapped.ui.theme.Inter

@Composable
fun UploadScreen(
    vm: StatsViewModel,
    onGenerate: () -> Unit
) {
    val context = LocalContext.current
    var uris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Selector
    val pickFiles = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { result ->
        uris = result
        if (result.isNotEmpty()) vm.loadFilesAsync(context, result)
    }

    val isLoaded = vm.loadedEntriesCount > 0
    val isLoading = vm.isLoading

    // ---------- Blink Animation (igual que antes) ----------
    val infiniteTransition = rememberInfiniteTransition()
    val alphaBlink by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        )
    )

    // ---------- Alternador de textos ----------
    val loadingMessages = listOf(
        "loading...",
        "this may take a few minutes..."
    )

    var currentMsgIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            while (isLoading) {
                kotlinx.coroutines.delay(1500)
                currentMsgIndex = (currentMsgIndex + 1) % loadingMessages.size
            }
        } else {
            currentMsgIndex = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ---------- TITULOS ----------
        Text(
            text = "FIRST, YOU NEED TO UPLOAD",
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 16.sp,
                letterSpacing = 1.2.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "YOUR  STATS",
            style = TextStyle(
                fontFamily = ChelseaMarket,
                fontSize = 42.sp,
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = Offset(2f, 2f),
                    blurRadius = 6f
                )
            ),
            modifier = Modifier.padding(bottom = 28.dp)
        )

        // ---------- BOTÓN PRINCIPAL (oculto cuando loading) ----------
        if (!isLoading) {
            Button(
                onClick = {
                    when {
                        !isLoaded && !isLoading -> {
                            pickFiles.launch(arrayOf("text/html"))
                        }
                        isLoaded && !isLoading -> {
                            vm.generateStats()
                            Log.d("Stats", "Generated stats: ${vm.stats.size}")
                            Log.d("StatsTest", "Loaded entries = ${vm.loadedEntriesCount}")
                            Log.d("StatsTest", "Generated stats = ${vm.stats.size}")
                            onGenerate()
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB100FF)
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (!isLoaded) "SELECT .HTML" else "GENERATE STATISTICS",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                )
            }

        }

        Spacer(Modifier.height(16.dp))

        // ---------- LOADING ANIMADO ----------
        if (isLoading) {
            Text(
                text = loadingMessages[currentMsgIndex],
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = alphaBlink)
                )
            )
        }
    }
}

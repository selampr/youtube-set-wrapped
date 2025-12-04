package com.selampr.youtube_set_wrapped.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.selampr.youtube_set_wrapped.ui.theme.ChelseaMarket
import com.selampr.youtube_set_wrapped.ui.theme.Inter

@Composable
fun UploadScreen(
    onSelectHtml: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 🔹 Texto superior
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
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onSelectHtml,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB100FF) // violeta neon
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_upload),
//                contentDescription = null,
//                tint = Color.White
//            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "SELECT .HTML",
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
}

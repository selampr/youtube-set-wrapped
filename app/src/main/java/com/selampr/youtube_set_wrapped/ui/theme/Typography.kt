package com.selampr.youtube_set_wrapped.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

val AppTypography = Typography(

    headlineLarge = TextStyle(
        fontFamily = ChelseaMarket,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Normal
    ),

    headlineMedium = TextStyle(
        fontFamily = ChelseaMarket,
        fontSize = 26.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Normal
    ),

    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal
    ),

    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal
    ),

    labelSmall = TextStyle(
        fontFamily = Inter,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
)

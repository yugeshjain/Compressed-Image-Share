package com.yugesh.compressedimageshare.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = CompressedImageSharingTypography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = black
    )
)

@Immutable
data class CompressedImageSharingTypography(
    val body1: TextStyle
)

inline val LocalTypography
    get() = compositionLocalOf { Typography }

package com.yugesh.compressedimageshare.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val Spacing = CompressedImageSharingSpacing(
    none = 0.dp,
    extraSmall = 4.dp,
    small = 8.dp,
    medium = 12.dp,
    large = 16.dp,
    extraLarge = 24.dp
)

@Immutable
data class CompressedImageSharingSpacing(
    val none: Dp,
    val extraSmall: Dp,
    val small: Dp,
    val medium: Dp,
    val large: Dp,
    val extraLarge: Dp
)

inline val LocalSpacing
    get() = compositionLocalOf { Spacing }

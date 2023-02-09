package com.yugesh.compressedimageshare.ui.theme

import androidx.annotation.RequiresPermission
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val CompressedImageSharingColorPalette = CompressedImageSharingColors(
    uiBackgroundPrimary = ui_background_primary,
    uiBackgroundSecondary = black,
    uiBackgroundTertiary = bg_tertiary,
    uiBackgroundGradient = listOf(bg_gradient_color_one, bg_gradient_color_two),
    textPrimary = white,
    textSecondary = black,
    textTertiary = gray_88,
    buttonBackground = button_bg_primary,
    buttonBackgroundGradient = listOf(button_gradient_color_one, button_gradient_color_three),
    error = error,
    uiBorder = black
)

@Composable
fun CompressedImageSharingTheme(
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colors = CompressedImageSharingColorPalette

    val colorPalette = remember {
        colors.copy()
    }
    colorPalette.update(other = colors)

    SideEffect {
        systemUiController.setSystemBarsColor(color = ui_background_primary)
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing,
        LocalColors provides colorPalette,
        LocalTypography provides Typography
    ) {
        MaterialTheme(
            typography = debugTypography(),
            shapes = Shapes,
            colors = debugColors(false)
        ) {
            CompositionLocalProvider(
                LocalRippleTheme provides RippleCustomTheme,
                content = content
            )
        }
    }
}

private object RippleCustomTheme: RippleTheme {
    @Composable
    override fun defaultColor() =
        RippleTheme.defaultRippleColor(
            gray_88,
            lightTheme = true
        )

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            gray_88,
            lightTheme = true
        )
}

object CompressedImageSharingTheme {
    val colors: CompressedImageSharingColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val spacing: CompressedImageSharingSpacing
        @Composable
        @RequiresPermission.Read
        get() = LocalSpacing.current

    val typography: CompressedImageSharingTypography
        @Composable
        @RequiresPermission.Read
        get() = LocalTypography.current
}

@Stable
class CompressedImageSharingColors(
    uiBackgroundPrimary: Color,
    uiBackgroundSecondary: Color,
    uiBackgroundTertiary: Color,
    uiBackgroundGradient: List<Color>,
    textPrimary: Color,
    textSecondary: Color,
    textTertiary: Color,
    buttonBackground: Color,
    buttonBackgroundGradient: List<Color>,
    error: Color,
    uiBorder: Color
) {
    var uiBackgroundPrimary by mutableStateOf(uiBackgroundPrimary)
        private set
    var uiBackgroundSecondary by mutableStateOf(uiBackgroundSecondary)
        private set
    var uiBackgroundTertiary by mutableStateOf(uiBackgroundTertiary)
        private set
    var uiBackgroundGradient by mutableStateOf(uiBackgroundGradient)
        private set
    var textPrimary by mutableStateOf(textPrimary)
        private set
    var textSecondary by mutableStateOf(textSecondary)
        private set
    var textTertiary by mutableStateOf(textTertiary)
        private set
    var buttonBackground by mutableStateOf(buttonBackground)
        private set
    var buttonBackgroundGradient by mutableStateOf(buttonBackgroundGradient)
        private set
    var error by mutableStateOf(error)
        private set
    var uiBorder by mutableStateOf(uiBorder)
        private set

    fun update(other: CompressedImageSharingColors) {
        uiBackgroundPrimary = other.uiBackgroundPrimary
        uiBackgroundSecondary = other.uiBackgroundSecondary
        uiBackgroundTertiary = other.uiBackgroundTertiary
        uiBackgroundGradient = other.uiBackgroundGradient
        textPrimary = other.textPrimary
        textSecondary = other.textSecondary
        textTertiary = other.textTertiary
        buttonBackground = other.buttonBackground
        buttonBackgroundGradient = other.buttonBackgroundGradient
        error = other.error
        uiBorder = other.uiBorder
    }

    fun copy(): CompressedImageSharingColors = CompressedImageSharingColors(
        uiBackgroundPrimary = uiBackgroundPrimary,
        uiBackgroundSecondary = uiBackgroundSecondary,
        uiBackgroundTertiary = uiBackgroundTertiary,
        uiBackgroundGradient = uiBackgroundGradient,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textTertiary = textTertiary,
        buttonBackground = buttonBackground,
        buttonBackgroundGradient = buttonBackgroundGradient,
        error = error,
        uiBorder = uiBorder
    )
}

val LocalColors = staticCompositionLocalOf<CompressedImageSharingColors> {
    error("No CompressedImageSharingColorPalette provided")
}

/**
 * A Material [Colors] implementation which sets all colors to [debugColor] to discourage usage of
 * [MaterialTheme.colors] in preference to [CompressedImageSharingTheme.colors].
 */
fun debugColors(
    darkTheme: Boolean,
    debugColor: Color = Color.Magenta
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !darkTheme
)

/**
 * A Material [Typography] implementation which sets all text's font size to 0.sp to discourage usage of
 * [MaterialTheme.typography] in preference to [CompressedImageSharingTheme.typography].
 */
fun debugTypography() = Typography(
    h1 = TextStyle(fontSize = 0.sp),
    h2 = TextStyle(fontSize = 0.sp),
    h3 = TextStyle(fontSize = 0.sp),
    h4 = TextStyle(fontSize = 0.sp),
    h5 = TextStyle(fontSize = 0.sp),
    h6 = TextStyle(fontSize = 0.sp),
    subtitle1 = TextStyle(fontSize = 0.sp),
    subtitle2 = TextStyle(fontSize = 0.sp),
    body1 = TextStyle(fontSize = 0.sp),
    body2 = TextStyle(fontSize = 0.sp),
    button = TextStyle(fontSize = 0.sp),
    caption = TextStyle(fontSize = 10.sp),
    overline = TextStyle(fontSize = 0.sp)
)

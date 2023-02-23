package com.yugesh.compressedimageshare.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme

/**
 * Snackbars provide brief messages about app processes at the bottom of the screen.
 *
 * Snackbars inform users of a process that an app has performed or will perform. They appear
 * temporarily, towards the bottom of the screen. They shouldn’t interrupt the user experience,
 * and they don’t require user input to disappear.
 *
 * A Snackbar can contain a single action. Because Snackbar disappears automatically, the action
 * shouldn't be "Dismiss" or "Cancel".
 *
 * ![Snackbars image](https://developer.android.com/images/reference/androidx/compose/material/snackbars.png)
 *
 * This components provides only the visuals of the [Snackbar]. If you need to show a [Snackbar]
 * with defaults on the screen, use [ScaffoldState.snackbarHostState] and
 * [SnackbarHostState.showSnackbar]:
 *
 * @param title defines the title of the SnackBar
 * @param modifier modifiers for the Snackbar layout
 * @param actionLabel defines the action Label.
 * @param actionOnNewLine whether or not action should be put on the separate line. Recommended
 * for action with long action text
 * @param elevation The z-coordinate at which to place the SnackBar. This controls the size
 * of the shadow below the SnackBar
 * @param shape Defines the Snackbar's shape as well as its shadow
 * @param backgroundColor background color of the Snackbar
 * @param contentColor color of the content to use inside the snackbar. Defaults to
 * either the matching content color for [backgroundColor], or, if it is not a color from
 * the theme, this will keep the same value set above this Surface.
 * @param onActionClick defines the click action to be performed on [actionLabel]
 */
@Composable
fun CISnackBar(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    actionOnNewLine: Boolean = false,
    elevation: Dp = CISnackBarDefaults.SnackBarElevation,
    shape: Shape = CISnackBarDefaults.SnackBarShape,
    backgroundColor: Color = CISnackBarDefaults.BackgroundColor,
    contentColor: Color = CISnackBarDefaults.ContentColor,
    onActionClick: () -> Unit
) {
    Snackbar(
        modifier = modifier.padding(CompressedImageSharingTheme.spacing.large),
        action = {
            if (!actionLabel.isNullOrEmpty()) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onActionClick()
                        }
                        .padding(end = 12.dp),
                    text = actionLabel,
                    style = CompressedImageSharingTheme.typography.body1,
                    color = CompressedImageSharingTheme.colors.error
                )
            }
        },
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        content = {
            Text(
                text = title,
                style = CompressedImageSharingTheme.typography.body1,
                color = CompressedImageSharingTheme.colors.textPrimary
            )
        }
    )
}

/** Contains the default values used by [CISnackBar]. */
object CISnackBarDefaults {

    /** The default SnackBar elevation. */
    val SnackBarElevation = 6.dp

    /** The default SnackBar shape. */
    val SnackBarShape
        @Composable
        get() = RoundedCornerShape(CompressedImageSharingTheme.spacing.extraSmall)

    /** The default SnackBar background color. */
    val BackgroundColor
        @Composable
        get() = CompressedImageSharingTheme.colors.uiBackgroundSecondary

    /** The default SnackBar content color. */
    val ContentColor
        @Composable
        get() = CompressedImageSharingTheme.colors.textPrimary
}

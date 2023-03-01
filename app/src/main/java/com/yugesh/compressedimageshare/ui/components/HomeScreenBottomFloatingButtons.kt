package com.yugesh.compressedimageshare.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme

@Composable
fun HomeScreenBottomFloatingButtons(
    buttonText: String,
    onShareClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isShareVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val horizontalArrangement = if (isShareVisible) Arrangement.Center else Arrangement.End
    val settingsButtonEndPadding: Dp by animateDpAsState(targetValue = if (isShareVisible) 0.dp else 16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .padding(bottom = 12.dp, end = settingsButtonEndPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        AnimatedVisibility(visible = isShareVisible) {
            Button(
                modifier = modifier,
                colors = ButtonDefaults.buttonColors(backgroundColor = CompressedImageSharingTheme.colors.buttonBackground),
                shape = CircleShape,
                onClick = onShareClick,
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        tint = CompressedImageSharingTheme.colors.textPrimary,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 8.dp, start = 24.dp)
                            .padding(vertical = 4.dp)
                            .size(20.dp)
                    )

                    Text(
                        text = buttonText,
                        fontSize = 16.sp,
                        color = CompressedImageSharingTheme.colors.textPrimary,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .padding(end = 24.dp)
                    )
                }
            )
        }

        AnimatedVisibility(visible = !isShareVisible) {
            Button(
                onClick = onSettingsClick,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = CompressedImageSharingTheme.colors.buttonBackground),
                shape = CircleShape,
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                        contentDescription = "",
                        tint = CompressedImageSharingTheme.colors.textPrimary,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .size(20.dp)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenBottomFloatingButtonsPreview() {
    CompressedImageSharingTheme {
        HomeScreenBottomFloatingButtons(
            buttonText = "",
            onShareClick = {},
            onSettingsClick = {},
            isShareVisible = false
        )
    }
}

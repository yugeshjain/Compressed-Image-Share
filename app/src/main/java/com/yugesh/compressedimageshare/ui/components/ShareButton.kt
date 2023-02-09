package com.yugesh.compressedimageshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme

@Composable
fun ShareButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(backgroundColor = CompressedImageSharingTheme.colors.buttonBackground),
            shape = CircleShape,
            onClick = onClick,
            content = {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    color = CompressedImageSharingTheme.colors.textPrimary,
                    modifier = Modifier.padding(
                        vertical = 4.dp,
                        horizontal = 24.dp
                    )
                )
            }
        )
    }
}

@Preview
@Composable
fun ShareButtonPreview() {
    CompressedImageSharingTheme {
        ShareButton(
            buttonText = "",
            onClick = {}
        )
    }
}

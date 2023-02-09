package com.yugesh.compressedimageshare.ui.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme

@Composable
fun AddImagesBoxButton(
    buttonText: String,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(vertical = 16.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(
                color = CompressedImageSharingTheme.colors.uiBackgroundSecondary.copy(
                    alpha = 0.3f
                )
            )
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                tint = CompressedImageSharingTheme.colors.textPrimary,
                contentDescription = stringResource(R.string.add_button),
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )

            Text(
                text = buttonText,
                fontSize = 16.sp,
                color = CompressedImageSharingTheme.colors.textPrimary
            )
        }
    }
}

@Preview
@Composable
private fun AddImagesBoxButtonPreview() {
//    CompressedImageSharingTheme {
//        AddImagesBoxButton(
//            buttonText = "",
//            photoPickerLauncher =
//        )
//    }
}

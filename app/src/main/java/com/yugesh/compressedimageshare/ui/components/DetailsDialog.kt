package com.yugesh.compressedimageshare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.ui.theme.black
import com.yugesh.compressedimageshare.ui.theme.white

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun DetailsDialog(
    originalImageSize: String = "",
    compressedImageSize: String = "",
    originalImageName: String = "",
    compressedImageName: String = "",
    onDismissRequest: () -> Unit = {}
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        content = {

            Surface(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = white,
                contentColor = black
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Card(
                            shape = CircleShape,
                            backgroundColor = CompressedImageSharingTheme.colors.buttonBackground.copy(
                                alpha = 0.7f
                            ),
                            onClick = onDismissRequest,
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_remove),
                                    contentDescription = stringResource(R.string.remove_image),
                                    tint = CompressedImageSharingTheme.colors.textPrimary,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(20.dp)
                                )
                            }
                        )
                    }
                    DialogTextRow(
                        rowTitle = "Original Image Name",
                        rowValue = originalImageName
                    )
                    DialogTextRow(
                        rowTitle = "Original Image Size",
                        rowValue = originalImageSize,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    DialogTextRow(
                        rowTitle = "Compressed Image Name",
                        rowValue = compressedImageName
                    )
                    DialogTextRow(
                        rowTitle = "Compressed Image Size",
                        rowValue = compressedImageSize
                    )
                }
            }
        }
    )
}

@Composable
fun DialogTextRow(
    rowTitle: String,
    rowValue: String,
    modifier: Modifier = Modifier
) {
    val fontSize = 16.sp
    Row(
        modifier = modifier
    ) {
        Text(
            text = rowTitle,
            color = black,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .padding(end = 4.dp)
        )

        Text(
            text = ":",
            color = black,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(end = 8.dp)
        )

        Text(
            text = rowValue,
            color = black,
            fontSize = fontSize
        )
    }
}

@Preview
@Composable
private fun DetailsDialogContentPreview() {
    CompressedImageSharingTheme {
        DetailsDialog()
    }
}

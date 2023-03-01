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
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.screens.HomeViewModel
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.ui.theme.black
import com.yugesh.compressedimageshare.ui.theme.white
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val compressionQuality by viewModel.compressionSliderValue.collectAsStateWithLifecycle()
    var sliderValue by remember {
        mutableStateOf(compressionQuality)
    }
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        content = {
            Surface(
                modifier = modifier
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

                    Text(
                        text = stringResource(R.string.settings),
                        style = CompressedImageSharingTheme.typography.title.copy(
                            color = CompressedImageSharingTheme.colors.textSecondary
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Compression Value: $sliderValue",
                            style = CompressedImageSharingTheme.typography.body1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Slider(
                            value = sliderValue.toFloat(),
                            onValueChange = {
                                sliderValue = it.roundToInt()
                            },
                            valueRange = 20f..100f,
                            onValueChangeFinished = {
                                viewModel.updateCompressionValue(compressionValue = sliderValue)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = CompressedImageSharingTheme.colors.buttonBackground,
                                activeTrackColor = CompressedImageSharingTheme.colors.buttonBackground,
                                inactiveTrackColor = CompressedImageSharingTheme.colors.buttonBackground.copy(
                                    alpha = 0.5f
                                ),
                            )
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun SettingsDialogPreview() {
    CompressedImageSharingTheme {
        SettingsDialog(
            onDismissRequest = {}
        )
    }
}

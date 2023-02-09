package com.yugesh.compressedimageshare.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.screens.CompressedFile
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.util.toKbOrMb

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SelectedImagesGrid(
    onRemoveClick: (Int) -> Unit,
    compressedImagesList: List<CompressedFile>,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(2),
        content = {
            itemsIndexed(compressedImagesList) { index, compressedFile ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(color = CompressedImageSharingTheme.colors.uiBackgroundPrimary),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Column {
                        AsyncImage(
                            model = compressedFile.uri,
                            contentDescription = stringResource(R.string.selected_image),
                            contentScale = ContentScale.FillBounds
                        )
                        Text(
                            text = "Original Size: " + compressedFile.originalSize.toKbOrMb(),
                            fontSize = 12.sp,
                            color = CompressedImageSharingTheme.colors.textPrimary
                        )
                        Text(
                            text = "Compressed Size: " + compressedFile.compressedSize.toKbOrMb(),
                            fontSize = 12.sp,
                            color = CompressedImageSharingTheme.colors.textPrimary
                        )
                    }

                    Card(
                        modifier = Modifier
                            .padding(4.dp),
                        shape = CircleShape,
                        backgroundColor = CompressedImageSharingTheme.colors.buttonBackground.copy(
                            alpha = 0.7f
                        ),
                        onClick = {
                            onRemoveClick(index)
                        },
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
            }
        }
    )
}

@Preview
@Composable
private fun SelectedImagesGridPreview() {
    CompressedImageSharingTheme {
        SelectedImagesGrid(
            onRemoveClick = {},
            compressedImagesList = emptyList()
        )
    }
}

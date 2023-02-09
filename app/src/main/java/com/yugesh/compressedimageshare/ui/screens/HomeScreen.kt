package com.yugesh.compressedimageshare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.util.toKbOrMb
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Photo Picker
    var selectedImages by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uri ->
        selectedImages = uri
    }

    LaunchedEffect(key1 = selectedImages) {
        // Gives out Compressed Files
        viewModel.compressImages(
            uriList = selectedImages,
            context = context
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        backgroundColor = CompressedImageSharingTheme.colors.uiBackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        CompressedImageSharingTheme.colors.uiBackgroundGradient
                    )
                )
                .padding(paddingValues = paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
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
                        tint = CompressedImageSharingTheme.colors.buttonBackground,
                        contentDescription = stringResource(R.string.add_button),
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                    )

                    Text(
                        text = stringResource(R.string.select_images_from_gallery),
                        fontSize = 16.sp,
                        color = CompressedImageSharingTheme.colors.textPrimary
                    )
                }
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                content = {
                    itemsIndexed(homeScreenUiState.compressedImages) { _, compressedFile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = 8.dp,
                            backgroundColor = CompressedImageSharingTheme.colors.uiBackgroundPrimary
                        ) {
                            Column {
                                Image(
                                    painter = rememberAsyncImagePainter(model = compressedFile.uri),
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
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = CompressedImageSharingTheme.colors.buttonBackground),
                    shape = CircleShape,
                    onClick = {
                        coroutineScope.launch {
                            val uriList =
                                viewModel.getUriListAsync(compressedImages = homeScreenUiState.compressedImages)
                                    .await()
                            viewModel.shareImage(
                                uriList = uriList,
                                context = context
                            )
                        }
                    },
                    content = {
                        Text(
                            text = "Share Image",
                            fontSize = 16.sp,
                            color = CompressedImageSharingTheme.colors.textSecondary,
                            modifier = Modifier.padding(
                                vertical = 4.dp,
                                horizontal = 24.dp
                            )
                        )
                    }
                )
            }
        }
    }
}

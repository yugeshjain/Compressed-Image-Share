package com.yugesh.compressedimageshare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yugesh.compressedimageshare.BuildConfig
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.ui.components.AddImagesBoxButton
import com.yugesh.compressedimageshare.ui.components.DetailsDialog
import com.yugesh.compressedimageshare.ui.components.SelectedImagesGrid
import com.yugesh.compressedimageshare.ui.components.ShareButton
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.ui.utils.CIScaffoldDefaults
import com.yugesh.compressedimageshare.ui.utils.InAppReviewManager
import com.yugesh.compressedimageshare.util.toKbOrMb
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    inAppReviewManager: InAppReviewManager,
    onAppUpdateSnackBarReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFlexibleUpdateDownloaded: Boolean,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsStateWithLifecycle()
    val showDetailsDialog by viewModel.showDetailsDialog.collectAsStateWithLifecycle()
    val selectedCompressedImages = homeScreenUiState.compressedImages

    // Photo Picker
    var selectedImages by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(30)
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

    LaunchedEffect(key1 = Unit){
        delay(120000)
        inAppReviewManager.requestReview()
    }

    if (showDetailsDialog.first) {
        showDetailsDialog.second?.let { ssIndex ->
            DetailsDialog(
                originalImageName = "Original File",
                originalImageSize = selectedCompressedImages.get(index = ssIndex).originalSize.toKbOrMb(),
                compressedImageName = "Compressed File",
                compressedImageSize = selectedCompressedImages.get(index = ssIndex).compressedSize.toKbOrMb(),
                onDismissRequest = {
                    viewModel.closeDetailDialog()
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = { CIScaffoldDefaults.GetSnackBarHost(snackbarHostState = it) },
        backgroundColor = CompressedImageSharingTheme.colors.uiBackgroundPrimary
    ) { paddingValues ->
        LaunchedEffect(key1 = isFlexibleUpdateDownloaded) {
            if (isFlexibleUpdateDownloaded) {
                val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = context.getString(R.string.compress_it_update_downloaded),
                    actionLabel = context.getString(R.string.reload),
                    duration = SnackbarDuration.Indefinite
                )
                when (snackBarResult) {
                    SnackbarResult.ActionPerformed -> onAppUpdateSnackBarReloadClick()
                    SnackbarResult.Dismissed -> {}
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.compress_it),
                    style = CompressedImageSharingTheme.typography.title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = BuildConfig.VERSION_NAME,
                    color = CompressedImageSharingTheme.colors.textPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                AddImagesBoxButton(
                    buttonText = stringResource(
                        if (selectedCompressedImages.isEmpty()) {
                            R.string.select_images_from_gallery
                        } else {
                            R.string.select_images_more_from_gallery
                        }
                    ),
                    photoPickerLauncher = photoPickerLauncher
                )
                if (homeScreenUiState.loading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = CompressedImageSharingTheme.colors.buttonBackground,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    AnimatedVisibility(visible = selectedCompressedImages.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_image),
                                contentDescription = stringResource(R.string.placeholder_image),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(300.dp)
                            )
                        }
                    }
                    AnimatedVisibility(visible = selectedCompressedImages.isNotEmpty()) {
                        SelectedImagesGrid(
                            compressedImagesList = selectedCompressedImages,
                            onRemoveClick = { index ->
                                viewModel.removeImage(index = index)
                            },
                            onImageClick = { index ->
                                viewModel.openDetailsDialog(index)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            AnimatedVisibility(visible = selectedCompressedImages.isNotEmpty()) {
                ShareButton(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 24.dp),
                    buttonText = stringResource(
                        if (selectedCompressedImages.size == 1) {
                            R.string.share_image
                        } else {
                            R.string.share_images
                        }
                    ),
                    onClick = {
                        coroutineScope.launch {
                            val uriList =
                                viewModel.getUriListAsync(compressedImages = selectedCompressedImages)
                                    .await()
                            viewModel.shareImage(
                                uriList = uriList,
                                context = context
                            )
                        }
                    }
                )
            }
        }
    }
}

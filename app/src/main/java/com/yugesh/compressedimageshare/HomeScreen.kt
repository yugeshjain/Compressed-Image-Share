package com.yugesh.compressedimageshare

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val homeScreenUiState by viewModel.homeScreenUiState.collectAsStateWithLifecycle()
    val imageHeight = LocalConfiguration.current.screenWidthDp.minus(48).div(3).dp

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
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Button",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Compressed Image Files Grid
                items(homeScreenUiState.compressedImages) { compressedFile ->
                    Column {
                        Image(
                            painter = rememberAsyncImagePainter(model = compressedFile.uri),
                            contentDescription = "Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(imageHeight),
                            contentScale = ContentScale.FillWidth
                        )
                        Text(text = "Original Size: " + compressedFile.originalSize.toKbOrMb())
                        Text(text = "Compressed Size: " + compressedFile.compressedSize.toKbOrMb())
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    shape = CircleShape,
                    onClick = {
                        val uriList = arrayListOf<Uri>()
                        homeScreenUiState.compressedImages.forEach {
                            uriList.add(it.uri)
                        }
                        shareImage(
                            uriList = uriList,
                            context = context
                        )
                    },
                    content = {
                        Text(
                            text = "Share Image",
                            fontSize = 24.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(
                                vertical = 12.dp,
                                horizontal = 24.dp
                            )
                        )
                    }
                )
            }
        }
    }
}

private fun shareImage(uriList: ArrayList<Uri>, context: Context) {
    if (uriList.isNotEmpty()) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
        }
        try {
            startActivity(
                context,
                Intent.createChooser(shareIntent, "Share Compressed Images"),
                Bundle()
            )
            uriList.clear()
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }
}

fun Long.toKbOrMb(): String {
    val convertToKb = this.div(1000)
    return if (convertToKb > 1024) {
        "${convertToKb.div(1024)}mb"
    } else {
        "${convertToKb}kb"
    }
}

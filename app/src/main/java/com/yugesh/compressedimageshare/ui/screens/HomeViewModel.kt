package com.yugesh.compressedimageshare.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Immutable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.util.Constants.PACKAGE_NAME
import com.yugesh.compressedimageshare.util.Constants.PROVIDER_EXTENSION
import com.yugesh.compressedimageshare.dispatchers.CoroutineDispatcherProvider
import com.yugesh.compressedimageshare.util.Constants.COMPRESSED_IMAGE_EXTENSION
import com.yugesh.compressedimageshare.util.Constants.COMPRESSED_IMAGE_PREFIX
import com.yugesh.compressedimageshare.util.Constants.TEMP_FILE_EXTENSION
import com.yugesh.compressedimageshare.util.Constants.TEMP_FILE_PREFIX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dispatchers: CoroutineDispatcherProvider
) : ViewModel() {

    // Home Screen Ui State
    private val _homeScreenUiState =
        MutableStateFlow(HomeScreenUiState(loading = false))
    val homeScreenUiState: StateFlow<HomeScreenUiState> =
        _homeScreenUiState.asStateFlow()

    fun compressImages(
        uriList: List<Uri>,
        context: Context
    ) {
        _homeScreenUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val compressedImagesList = convertFullListAsync(
                uriList = uriList,
                context = context
            ).await()
            _homeScreenUiState.update {
                it.copy(
                    compressedImages = compressedImagesList,
                    loading = false
                )
            }
        }
    }

    private fun convertFullListAsync(
        uriList: List<Uri>,
        context: Context
    ): Deferred<List<CompressedFile>> =
        viewModelScope.async(dispatchers.default) {
            val compressedImagesList = arrayListOf<CompressedFile>()
            uriList.forEach {
                val convertedFile = it.convertToFileAndCompressAsync(context = context).await()
                compressedImagesList.add(convertedFile)
            }
            compressedImagesList
        }

    private fun Uri.convertToFileAndCompressAsync(
        context: Context,
        quality: Int = 50
    ): Deferred<CompressedFile> =
        viewModelScope.async(dispatchers.io) {
            val imgFile = withContext(Dispatchers.IO) {
                File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_EXTENSION)
            }
            val inputStream =
                context.contentResolver.openInputStream(this@convertToFileAndCompressAsync)
            try {
                inputStream.use { input ->
                    imgFile.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        BitmapFactory.decodeFile(imgFile.absolutePath)
                    }
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                    val byteArray = stream.toByteArray()
                    val file = withContext(Dispatchers.IO) {
                        File.createTempFile(COMPRESSED_IMAGE_PREFIX, COMPRESSED_IMAGE_EXTENSION)
                    }
                    file.writeBytes(byteArray)
                    val compressedUri =
                        FileProvider.getUriForFile(
                            context,
                            PACKAGE_NAME + PROVIDER_EXTENSION,
                            file
                        )
                    return@async CompressedFile(
                        uri = compressedUri,
                        originalSize = imgFile.length(),
                        compressedSize = file.length()
                    )

                } catch (e: Exception) {
                    return@async CompressedFile(
                        uri = this@convertToFileAndCompressAsync,
                        originalSize = imgFile.length(),
                        compressedSize = imgFile.length()
                    )
                }
            } catch (e: Exception) {
                throw e
            }
        }

    fun getUriListAsync(compressedImages: List<CompressedFile>): Deferred<ArrayList<Uri>> =
        viewModelScope.async(dispatchers.io) {
            val uriList = arrayListOf<Uri>()
            compressedImages.forEach {
                uriList.add(it.uri)
            }
            uriList
        }


    fun shareImage(uriList: ArrayList<Uri>, context: Context) {
        if (uriList.isNotEmpty()) {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
            }
            try {
                ContextCompat.startActivity(
                    context,
                    Intent.createChooser(shareIntent, "Share Compressed Images"),
                    Bundle()
                )
                uriList.clear()
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_app_available),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

/**
 * Home Screen Ui State
 * */
@Immutable
data class HomeScreenUiState(
    val compressedImages: List<CompressedFile> = emptyList(),
    val loading: Boolean = false
)

data class CompressedFile(
    val uri: Uri,
    val originalSize: Long,
    val compressedSize: Long
)

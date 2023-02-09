package com.yugesh.compressedimageshare

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yugesh.compressedimageshare.dispatchers.CoroutineDispatcherProvider
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

    fun compressImagesToBitmap(
        uriList: List<Uri>,
        context: Context
    ) {
        _homeScreenUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val compressedBitmapsList = convertUriListToCompressedBitmapListAsync(
                uriList = uriList,
                context = context
            ).await()
            _homeScreenUiState.update {
                it.copy(
                    compressedBitmaps = compressedBitmapsList,
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

    private fun convertUriListToCompressedBitmapListAsync(
        uriList: List<Uri>,
        context: Context
    ): Deferred<List<CompressedBitmap>> =
        viewModelScope.async(dispatchers.default) {
            val compressedImagesList = arrayListOf<CompressedBitmap>()
            uriList.forEach {
                val convertedFile = it.compressedFileBitmapAsync(context = context).await()
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
                File.createTempFile("IMG-", ".jpg")
            }
            val inputStream =
                context.contentResolver.openInputStream(this@convertToFileAndCompressAsync)
            try {
                inputStream.use { input ->
                    imgFile.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                println("Original Image File: ${imgFile.length()}")
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        BitmapFactory.decodeFile(imgFile.absolutePath)
                    }
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                    val byteArray = stream.toByteArray()
                    val file = withContext(Dispatchers.IO) {
                        File.createTempFile("COMPRESSED-", ".jpg")
                    }
                    file.writeBytes(byteArray)
                    println("Compressed Image File: ${file.length()}")
                    val compressedUri =
                        FileProvider.getUriForFile(context, "com.yugesh.compressedimageshare" + ".provider", file)
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

    private fun Uri.compressedFileBitmapAsync(
        context: Context,
        quality: Int = 50
    ): Deferred<CompressedBitmap> =
        viewModelScope.async(dispatchers.io) {
            val imgFile = withContext(Dispatchers.IO) {
                File.createTempFile("IMG-", ".jpg")
            }
            val inputStream =
                context.contentResolver.openInputStream(this@compressedFileBitmapAsync)
            try {
                inputStream.use { input ->
                    imgFile.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                println("Original Image File: ${imgFile.length()}")
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        BitmapFactory.decodeFile(imgFile.absolutePath)
                    }
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                    val compressedBitmap = BitmapFactory.decodeByteArray(
                        stream.toByteArray(),
                        0,
                        stream.size()
                    )
                    return@async CompressedBitmap(
                        bitmap = compressedBitmap,
                        originalSize = (bitmap.rowBytes * bitmap.height).toLong(),
                        compressedSize = (compressedBitmap.rowBytes * compressedBitmap.height).toLong()
                    )
                } catch (e: Exception) {
                    throw e
                }
            } catch (e: Exception) {
                throw e
            }
        }
}

/**
 * Home Screen Ui State
 * */
@Immutable
data class HomeScreenUiState(
    val compressedImages: List<CompressedFile> = emptyList(),
    val compressedBitmaps: List<CompressedBitmap> = emptyList(),
    val loading: Boolean = false
)

data class CompressedFile(
    val uri: Uri,
    val originalSize: Long,
    val compressedSize: Long
)

data class CompressedBitmap(
    val bitmap: Bitmap,
    val originalSize: Long,
    val compressedSize: Long
)

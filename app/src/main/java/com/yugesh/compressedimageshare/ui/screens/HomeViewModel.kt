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
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.yugesh.compressedimageshare.BuildConfig
import com.yugesh.compressedimageshare.R
import com.yugesh.compressedimageshare.dispatchers.CoroutineDispatcherProvider
import com.yugesh.compressedimageshare.util.Constants.COMPRESSED_IMAGE_EXTENSION
import com.yugesh.compressedimageshare.util.Constants.COMPRESSED_IMAGE_PREFIX
import com.yugesh.compressedimageshare.util.Constants.PACKAGE_NAME
import com.yugesh.compressedimageshare.util.Constants.PROVIDER_EXTENSION
import com.yugesh.compressedimageshare.util.Constants.TEMP_FILE_EXTENSION
import com.yugesh.compressedimageshare.util.Constants.TEMP_FILE_PREFIX
import com.yugesh.compressedimageshare.util.inappupdates.AppUpdateState
import com.yugesh.compressedimageshare.util.inappupdates.InAppUpdatesConstants
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

    private var selectedImages: MutableList<CompressedFile> = mutableStateListOf()

    // Home Screen Ui State
    private val _homeScreenUiState =
        MutableStateFlow(HomeScreenUiState(loading = false))
    val homeScreenUiState: StateFlow<HomeScreenUiState> =
        _homeScreenUiState.asStateFlow()

    private val _flexibleUpdateDownloaded = MutableStateFlow(false)
    val flexibleUpdateDownloaded: StateFlow<Boolean> = _flexibleUpdateDownloaded.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _compressionSliderValue = MutableStateFlow(50)
    val compressionSliderValue: StateFlow<Int> = _compressionSliderValue.asStateFlow()

    private val _showDetailsDialog = MutableStateFlow<Pair<Boolean, Int?>>(Pair(false, null))
    val showDetailsDialog: StateFlow<Pair<Boolean, Int?>> = _showDetailsDialog.asStateFlow()

    private val currentAppVersion = BuildConfig.VERSION_CODE.toLong()

    private val _appUpdateState = MutableStateFlow(Pair(AppUpdateState.LOADING, currentAppVersion))
    val appUpdateState: StateFlow<Pair<AppUpdateState, Long>> = _appUpdateState.asStateFlow()

    fun handleDownloadedFlexibleAppUpdate(isDownloaded: Boolean) {
        _flexibleUpdateDownloaded.value = isDownloaded
    }

    fun openDetailsDialog(index: Int) {
        _showDetailsDialog.value = Pair(true, index)
    }

    fun closeDetailDialog() {
        _showDetailsDialog.value = Pair(true, null)
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun updateCompressionValue(compressionValue: Int){
        _compressionSliderValue.value = compressionValue
    }

    fun compressImages(
        uriList: List<Uri>,
        context: Context,
        quality: Int
    ) {
        _homeScreenUiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val compressedImagesList = convertFullListAsync(
                uriList = uriList,
                context = context,
                quality = quality
            ).await()
            selectedImages += compressedImagesList
            _homeScreenUiState.update {
                it.copy(
                    compressedImages = selectedImages,
                    loading = false
                )
            }
        }
    }

    fun removeImage(index: Int) {
        selectedImages.removeAt(index = index)
        _homeScreenUiState.update {
            it.copy(
                compressedImages = selectedImages
            )
        }
    }

    private fun convertFullListAsync(
        uriList: List<Uri>,
        quality: Int,
        context: Context
    ): Deferred<List<CompressedFile>> =
        viewModelScope.async(dispatchers.default) {
            val compressedImagesList = arrayListOf<CompressedFile>()
            uriList.forEach {
                val convertedFile = it.convertToFileAndCompressAsync(context = context, quality = quality).await()
                compressedImagesList.add(convertedFile)
            }
            compressedImagesList
        }

    private fun Uri.convertToFileAndCompressAsync(
        context: Context,
        quality: Int,
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

    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    fun fetchUpdateTypeFromRemoteConfig(analytics: FirebaseAnalytics) {
        _appUpdateState.value = Pair(AppUpdateState.LOADING, currentAppVersion)
        viewModelScope.launch {
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updateVersion =
                        remoteConfig.getLong(InAppUpdatesConstants.APP_UPDATE_VERSION)
                    val isForceUpdate =
                        remoteConfig.getBoolean(InAppUpdatesConstants.FORCE_UPDATE_REQUIRED)

                    val params = Bundle().apply { putString(updateVersion.toString(), currentAppVersion.toString() ) }
                    analytics.logEvent("App Versions -> Force update($isForceUpdate)", params)
                    analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                        Bundle().apply { putString(updateVersion.toString(), currentAppVersion.toString() ) }
                    }

                    println("VERSION _ $updateVersion $isForceUpdate $currentAppVersion")

                    _appUpdateState.value = if (updateVersion > currentAppVersion) {
                        if (isForceUpdate) {
                            Pair(AppUpdateState.FORCE, updateVersion)
                        } else {
                            Pair(AppUpdateState.FLEXIBLE, updateVersion)
                        }
                    } else {
                        Pair(AppUpdateState.NO_UPDATE, currentAppVersion)
                    }
                } else {
                    Pair(AppUpdateState.NO_UPDATE, currentAppVersion)
                }
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

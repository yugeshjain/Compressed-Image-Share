package com.yugesh.compressedimageshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.yugesh.compressedimageshare.ui.screens.HomeScreen
import com.yugesh.compressedimageshare.ui.screens.HomeViewModel
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.ui.utils.InAppReviewManager
import com.yugesh.compressedimageshare.util.inappupdates.InAppUpdateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var inAppUpdate: InAppUpdateManager
    private lateinit var analytics: FirebaseAnalytics
    @Inject
    lateinit var inAppReviewManager: InAppReviewManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = Firebase.analytics
        viewModel.fetchUpdateTypeFromRemoteConfig(analytics = analytics)

        lifecycleScope.launch {
            viewModel.appUpdateState.collect {
                inAppUpdate = InAppUpdateManager(
                    activity = this@MainActivity,
                    updateType = it.first,
                    updateVersion = it.second,
                    onFlexibleUpdateDownloaded = { isFlexibleUpdateDownloaded ->
                        viewModel.handleDownloadedFlexibleAppUpdate(isDownloaded = isFlexibleUpdateDownloaded)
                    }
                )
            }
        }

        setContent {
            CompressedImageSharingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val isFlexibleUpdateDownloaded by viewModel.flexibleUpdateDownloaded.collectAsStateWithLifecycle()
                    HomeScreen(
                        onAppUpdateSnackBarReloadClick = { inAppUpdate.onComplete() },
                        isFlexibleUpdateDownloaded = isFlexibleUpdateDownloaded,
                        inAppReviewManager = inAppReviewManager
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate.onDestroy()
    }
}

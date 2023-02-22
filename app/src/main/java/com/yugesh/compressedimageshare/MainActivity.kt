package com.yugesh.compressedimageshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.yugesh.compressedimageshare.ui.screens.HomeScreen
import com.yugesh.compressedimageshare.ui.screens.HomeViewModel
import com.yugesh.compressedimageshare.ui.theme.CompressedImageSharingTheme
import com.yugesh.compressedimageshare.util.inappupdates.AppUpdateState
import com.yugesh.compressedimageshare.util.inappupdates.InAppUpdateManager
import com.yugesh.compressedimageshare.util.inappupdates.MyDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var inAppUpdate: InAppUpdateManager
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = Firebase.analytics
        viewModel.fetchUpdateTypeFromRemoteConfig(analytics = analytics)

        var appUpdateType = AppUpdateState.NO_UPDATE
        var appUpdateVersion = 1000L
        lifecycleScope.launch {
            viewModel.appUpdateState.collect {
                appUpdateType = it.first
                appUpdateVersion = it.second

                inAppUpdate = InAppUpdateManager(
                    activity = this@MainActivity,
                    updateType = it.first,
                    updateVersion = it.second
                )
            }
        }

        setContent {

            CompressedImageSharingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyDialog(appUpdateManager = inAppUpdate)
                    HomeScreen(
                        activity = this,
                        analytics = analytics
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate.onActivityResult(requestCode,resultCode, data)
    }
}

package com.yugesh.compressedimageshare.util.inappupdates

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.yugesh.compressedimageshare.BuildConfig
import com.yugesh.compressedimageshare.ui.screens.HomeViewModel
import com.yugesh.compressedimageshare.ui.theme.white

/**
 * In app update manager
 *
 * @constructor
 *
 * @param activity
 * @param updateType specifies FORCED, FLEXIBLE, NO_UPDATES or LOADING
 * @param updateVersion is the new version code fetched from Remote Config
 */
class InAppUpdateManager(
    activity: Activity,
    updateType: AppUpdateState,
    updateVersion: Long,
    viewModel: HomeViewModel
) : InstallStateUpdatedListener {

    private var appUpdateManager: AppUpdateManager
    private val myRequestCode = 500
    private var parentActivity: Activity = activity

    private var currentType = AppUpdateType.FLEXIBLE
    private val currentVersion = BuildConfig.VERSION_CODE

    init {
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

            // Check if the device have Play Services or not
            if (GoogleApiAvailabilityLight.getInstance()
                    .isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS
            ) {

                // Check if update is available from remote config and Play Store
                if (updateVersion > currentVersion && appUpdateInfo.updateAvailability()
                    == UpdateAvailability.UPDATE_AVAILABLE
                ) {

                    // Defines what to do with different type of updates
                    when (updateType) {

                        /* Starts the Immediate Update Flow and
                         disables the user from using the app */
                        AppUpdateState.FORCE -> {
                            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                startUpdate(
                                    appUpdateInfo = appUpdateInfo,
                                    updateType = AppUpdateType.IMMEDIATE
                                )
                            }
                        }

                        /* Starts the Flexible Update Flow and
                         user can choose to deny update and normal app
                         flow will be launched */
                        AppUpdateState.FLEXIBLE -> {
                            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                startUpdate(
                                    appUpdateInfo = appUpdateInfo,
                                    updateType = AppUpdateType.FLEXIBLE
                                )
//                                if (appUpdateInfo.installStatus() == InstallStatus.INSTALLING) {
//                                    appUpdateManager.registerListener(listener)
//                                }
//                                if (appUpdateInfo.installStatus() == InstallStatus.INSTALLED) {
//                                    appUpdateManager.unregisterListener(listener)
//                                }
                            }
                        }

                        else -> {
                            /* If the Update Type == AppUpdateState.LOADING or
                             Update Type == AppUpdateState.NO_UPDATE then normal
                             app flow will continue without blocking anything */
                        }
                    }
                }
            }
        }
        appUpdateManager.registerListener(this)
    }

    private fun startUpdate(
        appUpdateInfo: AppUpdateInfo,
        updateType: Int
    ) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateType,
                parentActivity,
                myRequestCode

            )
            currentType = updateType
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    fun onResume(viewModel: HomeViewModel) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (currentType == AppUpdateType.FLEXIBLE) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    viewModel.something.value = true
//                    showCompleteUpdateDialog()
                }
            } else if (currentType == AppUpdateType.IMMEDIATE) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                }
            }
        }
    }

    fun showCompleteUpdateDialog() {
        AlertDialog.Builder(parentActivity)
            .setTitle("Update Downloaded")
            .setMessage("An Update has been downloaded. Would you like to install it now?")
            .setPositiveButton("yes") { _, _ ->
                appUpdateManager.completeUpdate()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun flexibleUpdateDownloadCompleted() {
        AlertDialog.Builder(parentActivity)
            .setTitle("Update Downloaded")
            .setMessage("An Update has been downloaded. Would you like to install it now?")
            .setPositiveButton("yes") { _, _ ->
                appUpdateManager.completeUpdate()
            }
            .setNegativeButton("No", null)
            .show()
//        Snackbar.make(
//            parentActivity.window.decorView.rootView,
//            "An update has just been downloaded.",
//            Snackbar.LENGTH_INDEFINITE
//        ).apply {
//            setAction("RESTART") { appUpdateManager.completeUpdate() }
//            setActionTextColor(Color.WHITE)
//            show()
//        }
    }

    fun onComplete() {
        appUpdateManager.completeUpdate()
    }

    fun onDestroy() {
        appUpdateManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showCompleteUpdateDialog()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == myRequestCode) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                // If the update is cancelled or fails, you can request to start the update again.
                Log.e("ERROR", "Update flow failed! Result code: $resultCode")
            }
        }
    }
}

@Composable
fun MyDialog(appUpdateManager: InAppUpdateManager) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        content = {
            Column {
                Button(onClick = { appUpdateManager.onComplete() }) {
                    Text(text = "Update", color = white, fontSize = 24.sp)
                }
            }
        }
    )
}

package com.yugesh.compressedimageshare.util.inappupdates

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.yugesh.compressedimageshare.BuildConfig

/**
 * In app update manager
 *
 * @constructor
 *
 * @param activity
 * @param updateType specifies FORCED, FLEXIBLE, NO_UPDATES or LOADING
 * @param updateVersion is the new version code fetched from Remote Config
 * @param onFlexibleUpdateDownloaded is a callback that send a boolean to show a snackbar on successfull update download
 */
class InAppUpdateManager(
    activity: Activity,
    updateType: AppUpdateState,
    updateVersion: Long,
    private val onFlexibleUpdateDownloaded: (Boolean) -> Unit
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

    /* Starts an update based the type of updates available,
       which are Immediate or Flexible
     */
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* Handles the cases if a user goes to background
       and opens the app again while updating
       This is called in the onResume() of MainActivity
    * */
    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when (currentType) {
                AppUpdateType.FLEXIBLE -> {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        onFlexibleUpdateDownloaded(true)
                    }
                }

                AppUpdateType.IMMEDIATE -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                    }
                }
            }
        }
    }

    // Call this on the click of reload from Snackbar to install the downloaded update
    fun onComplete() {
        appUpdateManager.completeUpdate()
    }

    /* Unregistering the App Update Manager
       This is called in the onDestroy() of MainActivity
     */
    fun onDestroy() {
        appUpdateManager.unregisterListener(this)
    }

    // Shows the Snackbar to reload the App after the update is Downloaded
    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            onFlexibleUpdateDownloaded(true)
        }
    }
}

//package com.yugesh.compressedimageshare.util
//
//import android.app.Activity
//import android.content.IntentSender
//import com.google.android.gms.tasks.Task
//import com.google.android.play.core.appupdate.AppUpdateInfo
//import com.google.android.play.core.appupdate.AppUpdateManager
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.InstallStateUpdatedListener
//import com.google.android.play.core.install.model.AppUpdateType
//import com.yugesh.compressedimageshare.util.inappupdates.AppUpdateState
//
//class UpdateManagerImpl(private val activity: Activity) : UpdateManager {
//
//    private val appUpdateManager: AppUpdateManager by lazy {
//        AppUpdateManagerFactory.create(activity)
//    }
//
//    override fun getUpdateInfo(): Task<AppUpdateInfo> {
//        return appUpdateManager.appUpdateInfo
//    }
//
//    override fun startImmediateUpdateFlow(
//        appUpdateInfo: AppUpdateInfo,
//        requestCode: Int,
//        listner: InstallStateUpdatedListener
//    ) {
//        try {
//            appUpdateManager.startUpdateFlowForResult(
//                appUpdateInfo,
//                AppUpdateType.IMMEDIATE,
//                activity,
//                requestCode
//            )
//        } catch (e: IntentSender.SendIntentException) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun startFlexibleUpdateFlow(
//        appUpdateInfo: AppUpdateInfo,
//        listner: InstallStateUpdatedListener
//    ) {
//        try {
//            appUpdateManager.startUpdateFlowForResult(
//                appUpdateInfo,
//                AppUpdateType.FLEXIBLE,
//                activity,
//                0
//            )
//        } catch (e: IntentSender.SendIntentException) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun unregisterListner(listner: InstallStateUpdatedListener) {
//        appUpdateManager.unregisterListener(listner)
//    }
//
//    override fun completeUpdate(){
//        appUpdateManager.completeUpdate()
//    }
//}

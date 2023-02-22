//package com.yugesh.compressedimageshare.util
//
//import com.google.android.gms.tasks.Task
//import com.google.android.play.core.appupdate.AppUpdateInfo
//import com.google.android.play.core.install.InstallStateUpdatedListener
//
//interface UpdateManager {
//    fun getUpdateInfo(): Task<AppUpdateInfo>
//    fun startImmediateUpdateFlow(
//        appUpdateInfo: AppUpdateInfo,
//        requestCode: Int,
//        listner: InstallStateUpdatedListener
//    )
//    fun startFlexibleUpdateFlow(
//        appUpdateInfo: AppUpdateInfo,
//        listner: InstallStateUpdatedListener
//    )
//    fun unregisterListner(listner: InstallStateUpdatedListener)
//
//    fun completeUpdate()
//}

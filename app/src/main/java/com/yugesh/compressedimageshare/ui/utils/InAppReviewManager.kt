package com.yugesh.compressedimageshare.ui.utils

import android.app.Activity
import android.util.Log
import com.google.android.play.core.review.ReviewManager
import javax.inject.Inject

class InAppReviewManager @Inject constructor(
    private val reviewManager: ReviewManager
) {
    fun requestReview(activity: Activity) {
        val reviewRequest = reviewManager.requestReviewFlow()
        reviewRequest.addOnCompleteListener { request ->
            if (request.isSuccessful){
                val reviewInfo = request.result
                try {
                    val reviewFlow = reviewManager.launchReviewFlow(activity, reviewInfo)
                    reviewFlow.addOnCompleteListener {}
                } catch (e: Exception){
                    Log.e("e", e.stackTraceToString())
                }
            }
        }
    }
}

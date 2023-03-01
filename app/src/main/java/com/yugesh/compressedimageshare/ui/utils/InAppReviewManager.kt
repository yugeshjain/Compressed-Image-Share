package com.yugesh.compressedimageshare.ui.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManager
import javax.inject.Inject

class InAppReviewManager @Inject constructor(
    private val reviewManager: ReviewManager,
    private val context: Context
) {
    fun requestReview() {
        val reviewRequest = reviewManager.requestReviewFlow()
        val activity = context as Activity
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

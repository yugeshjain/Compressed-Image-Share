package com.yugesh.compressedimageshare.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsManager(
    eventName: String,
    paramsKey: String,
    paramsValue: String
) {
    private var analytics: FirebaseAnalytics = Firebase.analytics

    init {
        analytics.logEvent(
            eventName,
            Bundle().apply { putString(paramsKey, paramsValue) }
        )
    }
}

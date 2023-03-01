package com.yugesh.compressedimageshare.ui.utils

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object InAppReviewModule {
    @Provides
    @ActivityScoped
    fun provideInAppReviewManager(
        reviewManager: ReviewManager,
        @ActivityContext context: Context
    ): InAppReviewManager = InAppReviewManager(reviewManager, context)

    @Provides
    @ActivityScoped
    fun provideReviewManager(
        @ApplicationContext context: Context
    ): ReviewManager {
        return ReviewManagerFactory.create(context)
    }
}

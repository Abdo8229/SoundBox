package com.example.soundbox.views

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.soundbox.R
import com.google.android.material.progressindicator.LinearProgressIndicator

class MediaPlayerView(rootView: View) {
    private var mBottomSheet: FrameLayout
    private var mControlsContainer: ConstraintLayout
    private var mRootView: View
    private var mState: Int = 0

    init {
        this.mRootView = rootView
        this.mBottomSheet = findViewById(R.id.media_player_bottom_sheet_behavior) as FrameLayout
        this.mControlsContainer =
            findViewById(R.id.media_player_controls_container) as ConstraintLayout

        this.mRootView.alpha = 0.0F
    }

    companion object {
        const val STATE_NORMAL = 0
        const val STATE_PARTIAL = 1

    }

    fun findViewById(@IdRes id: Int): View {
        return this.mRootView.findViewById(id)
    }

    fun onSliding(slideOffset: Float, state: Int) {
        val fadeStart = 0.25F
        val alpha: Float = (slideOffset - fadeStart) * (1F / (1F - fadeStart))
        if (state == STATE_NORMAL) {
            this.mRootView.alpha = alpha
            this.mControlsContainer.alpha = 1F
        } else {
            this.mControlsContainer.alpha = (1F - alpha)
        }
        this.mState = state

    }

}
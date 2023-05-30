package com.example.soundbox.views

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.soundbox.R
import com.google.android.material.progressindicator.LinearProgressIndicator

class MediaPlayerBarView(rootView : View) {
    private var mBackgroundView: FrameLayout
    private var mProgressIndicator: LinearProgressIndicator
    private var mControlsContainer: ConstraintLayout
    private var mRootView: View
    private var mState:Int = 0

    init {
        this.mRootView = rootView
        this.mBackgroundView = findViewById(R.id.media_player_bar_bg) as FrameLayout
        this.mControlsContainer =
            findViewById(R.id.media_player_bar_controls_constraint) as ConstraintLayout
        this.mProgressIndicator = findViewById(R.id.media_player_bar_progress_indicator) as LinearProgressIndicator

        this.mRootView.alpha = 1.0F
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
        val alpha: Float = (slideOffset / fadeStart)
        if (state == STATE_NORMAL) {
            this.mRootView.alpha = 1F - alpha
            this.mBackgroundView.alpha = 1F
            this.mProgressIndicator.alpha = 1F
            this.mControlsContainer.alpha = 1F

        }else{
            this.mControlsContainer.alpha = ( alpha )
            this.mBackgroundView.alpha = 0F
            this.mProgressIndicator.alpha = 0F
            this.mControlsContainer.alpha = 1F
        }
        this.mState = state

    }
}
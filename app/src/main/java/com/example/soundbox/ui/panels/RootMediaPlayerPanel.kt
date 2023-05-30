package com.example.soundbox.ui.panels

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.extensions.bottomsheet.CustomBottomSheetBehaviorJava
import com.example.soundbox.R
import com.example.soundbox.databinding.LayoutRootMediaPlayerBinding
import com.realgear.multislidinguppanel.BasePanelView
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout

@SuppressLint("ViewConstructor")
class RootMediaPlayerPanel(context: Context, panelLayout: MultiSlidingUpPanelLayout) :
    BasePanelView(context, panelLayout) {
    private var binding: LayoutRootMediaPlayerBinding

    init {
        getContext().setTheme(R.style.Base_Theme_SoundBox)
        binding =
            LayoutRootMediaPlayerBinding.inflate(LayoutInflater.from(getContext()), this, true)
    }

    override fun onCreateView() {
        this.panelState = MultiSlidingUpPanelLayout.COLLAPSED
// The panel will slide up and down
        this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL)
// Sets the panels peak height
        this.peakHeight = resources.getDimensionPixelSize(R.dimen.media_player_height)
    }

    override fun onBindView() {
        val dm = resources.displayMetrics
        val frameLayout: FrameLayout = findViewById(R.id.media_player_bottom_sheet_behavior)
        val params: ViewGroup.LayoutParams = frameLayout.layoutParams
        params.height = dm.heightPixels - (mPeakHeight)
        frameLayout.layoutParams = params

        val customBottomSheetBehavior: CustomBottomSheetBehaviorJava<FrameLayout> =
            CustomBottomSheetBehaviorJava.from(frameLayout)
        customBottomSheetBehavior.skipAnchored = false
        customBottomSheetBehavior.allowUserDragging = true
        customBottomSheetBehavior.anchorOffset = dm.heightPixels * 0.75F.toInt()
        customBottomSheetBehavior.setMediaPlayerBarHeight(peakHeight)
        customBottomSheetBehavior.state = CustomBottomSheetBehaviorJava.STATE_COLLAPSED

        customBottomSheetBehavior.addBottomSheetCallback(object : CustomBottomSheetBehaviorJava.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, oldState: Int, newState: Int) {
                when (newState) {
                    CustomBottomSheetBehaviorJava.STATE_ANCHORED -> {

                    }

                    CustomBottomSheetBehaviorJava.STATE_EXPANDED -> {

                    }

                    CustomBottomSheetBehaviorJava.STATE_DRAGGING -> {
                        multiSlidingUpPanel.isSlidingEnabled = false
                        return
                    }

                    else -> {
                        multiSlidingUpPanel.isSlidingEnabled = true
                    }


                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do nothing

            }
        })

    }

    override fun onPanelStateChanged(panelSate: Int) {
    }

}
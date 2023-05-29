package com.example.soundbox.ui.panels

import android.content.Context
import android.view.LayoutInflater
import com.example.soundbox.R
import com.example.soundbox.databinding.RootNavigationBarBinding
import com.realgear.multislidinguppanel.BasePanelView
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout

class RootNavigationBarPanel(context: Context, panelLayout: MultiSlidingUpPanelLayout) :
    BasePanelView(context, panelLayout) {
    private var binding: RootNavigationBarBinding


    init {
        getContext().setTheme(R.style.Base_Theme_SoundBox)
        binding = RootNavigationBarBinding .inflate(LayoutInflater.from(getContext()), this, true)
    }

    override fun onCreateView() {
        this.panelState = MultiSlidingUpPanelLayout.COLLAPSED
// The panel will slide up and down
        this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL)
// Sets the panels peak height
        this.peakHeight = resources.getDimensionPixelSize(R.dimen.navigation_bar_height)
    }

    override fun onBindView() {
    }

    override fun onPanelStateChanged(panelSate: Int) {
    }

}
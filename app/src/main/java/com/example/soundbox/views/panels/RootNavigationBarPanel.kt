package com.example.soundbox.views.panels

import android.content.Context
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.example.soundbox.R
import com.example.soundbox.databinding.RootNavigationBarBinding
import com.example.soundbox.ui.adapters.StateFragmentAdapter
import com.example.soundbox.ui.fragments.home.FragmentHome
import com.example.soundbox.ui.fragments.library.FragmentLibrary
import com.example.soundbox.ui.fragments.settings.FragmentSettings
import com.realgear.multislidinguppanel.BasePanelView
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout
import com.realgear.readable_bottom_bar.ReadableBottomBar

class RootNavigationBarPanel(context: Context, panelLayout: MultiSlidingUpPanelLayout) :
    BasePanelView(context, panelLayout) {

    private var binding: RootNavigationBarBinding
    private lateinit var rootViewPager: ViewPager2
    private lateinit var rootNavBar: ReadableBottomBar


    init {
        getContext().setTheme(R.style.Base_Theme_SoundBox)
        binding = RootNavigationBarBinding .inflate(LayoutInflater.from(getContext()), this, true)
    }

    private fun initViewPager2AndNavBar(){
        rootViewPager = multiSlidingUpPanel.findViewById(R.id.root_view_pager)
        rootNavBar = findViewById(R.id.root_navigation_bar)
        val adapter = StateFragmentAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(FragmentHome())
        adapter.addFragment(FragmentLibrary())
        adapter.addFragment(FragmentSettings())

        rootViewPager.adapter = adapter
        rootNavBar.setupWithViewPager2(rootViewPager)
    }
    override fun onCreateView() {
        this.panelState = MultiSlidingUpPanelLayout.COLLAPSED
// The panel will slide up and down
        this.setSlideDirection(MultiSlidingUpPanelLayout.SLIDE_VERTICAL)
// Sets the panels peak height
        this.peakHeight = resources.getDimensionPixelSize(R.dimen.navigation_bar_height)
    }

    override fun onBindView() {
        initViewPager2AndNavBar()
    }

    override fun onPanelStateChanged(panelSate: Int) {
    }

}
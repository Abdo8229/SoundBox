package com.example.soundbox.views.panels

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.extensions.bottomsheet.CustomBottomSheetBehaviorJava
import com.example.soundbox.R
import com.example.soundbox.databinding.LayoutRootMediaPlayerBinding
import com.example.soundbox.ui.adapters.StateFragmentAdapter
import com.example.soundbox.ui.fragments.home.FragmentHome
import com.example.soundbox.ui.fragments.library.FragmentLibrary
import com.example.soundbox.ui.fragments.settings.FragmentSettings
import com.example.soundbox.views.MediaPlayerBarView
import com.example.soundbox.views.MediaPlayerView
import com.realgear.multislidinguppanel.BasePanelView
import com.realgear.multislidinguppanel.IPanel
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout
import com.realgear.readable_bottom_bar.ReadableBottomBar

@SuppressLint("ViewConstructor")
class RootMediaPlayerPanel(context: Context, panelLayout: MultiSlidingUpPanelLayout) :
    BasePanelView(context, panelLayout) {
    private var binding: LayoutRootMediaPlayerBinding
//    private lateinit var rootViewPager: ViewPager2
//    private lateinit var rootNavBar: ReadableBottomBar
    private lateinit var mMediaPlayerBarView : MediaPlayerBarView
    private lateinit var mMediaPlayerView : MediaPlayerView
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
    private fun intitMediaPlayers(){
        mMediaPlayerView =
            MediaPlayerView(findViewById(R.id.media_player_view))
        mMediaPlayerBarView =
            MediaPlayerBarView(findViewById(R.id.media_player_bar_view))
    }
    private fun initViewPager2AndNavBar(){
//        rootViewPager = findViewById(R.id.root_view_pager)
//        rootNavBar = findViewById(R.id.root_navigation_bar)
//        val adapter = StateFragmentAdapter(supportFragmentManager, lifecycle)
//        adapter.addFragment(FragmentHome())
//        adapter.addFragment(FragmentLibrary())
//        adapter.addFragment(FragmentSettings())
//
//        rootViewPager.adapter = adapter
//        rootNavBar.setupWithViewPager2(rootViewPager)
    }
    override fun onBindView() {
        intitMediaPlayers()
//        initViewPager2AndNavBar()

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

        customBottomSheetBehavior.addBottomSheetCallback(object :
            CustomBottomSheetBehaviorJava.BottomSheetCallback() {
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
                mMediaPlayerView.onSliding(slideOffset,MediaPlayerView.STATE_PARTIAL)
                mMediaPlayerBarView.onSliding(slideOffset,MediaPlayerBarView.STATE_PARTIAL)


            }
        })

    }

    override fun onPanelStateChanged(panelSate: Int) {
    }

    override fun onSliding(panel: IPanel<View>, top: Int, dy: Int, slidingOffset: Float) {
        super.onSliding(panel, top, dy, slidingOffset)

        mMediaPlayerView.onSliding(slidingOffset,MediaPlayerView.STATE_NORMAL)
        mMediaPlayerBarView.onSliding(slidingOffset,MediaPlayerBarView.STATE_NORMAL)
    }
}
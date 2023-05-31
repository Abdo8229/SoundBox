package com.example.soundbox.ui

import android.view.View
import androidx.annotation.IdRes
import com.example.soundbox.MainActivity
import com.example.soundbox.R
import com.example.soundbox.views.panels.RootMediaPlayerPanel
import com.example.soundbox.views.panels.RootNavigationBarPanel
import com.realgear.multislidinguppanel.Adapter
import com.realgear.multislidinguppanel.MultiSlidingUpPanelLayout
import com.realgear.multislidinguppanel.PanelStateListener

class UIThread(activity : MainActivity) {
//    private lateinit var m_vMultiSlidingPanel :  MultiSlidingUpPanelLayout
    private  var m_vMainActivity : MainActivity
    init {
        this.m_vMainActivity = activity
        onCreate()
    }
   private fun onCreate(){
        val panelLayout : MultiSlidingUpPanelLayout = findViewById(R.id.root_sliding_up_panel) as MultiSlidingUpPanelLayout
        val items: MutableList<Class<*>> = arrayListOf(
            RootMediaPlayerPanel::class.java,
            RootNavigationBarPanel::class.java
        )
        /*** You add your panels here it can be different classes with different layouts
        // but they all should extend the BasePanelView class with same constructors
        // You can add 1 or more then 1 panels
        // You add your panels here it can be different classes with different layouts
        // but they all should extend the BasePanelView class with same constructors
        // You can add 1 or more then 1 panels


        // This is to listen on all the panels you can add methods or extend the class
        // This is to listen on all the panels you can add methods or extend the class***/
        panelLayout.setPanelStateListener(object :
            PanelStateListener(panelLayout) {


        })
        /*** The adapter handles the items you can also extend it but I don't recommend for
        // beginners
        // The adapter handles the items you can also extend it but I don't recommend for
        // beginners**/
       panelLayout.adapter =object : Adapter(this.m_vMainActivity,items){}


    }
    private fun findViewById(@IdRes id: Int): View {
        return this.m_vMainActivity.findViewById(id)
    }
}
package com.example.soundbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.soundbox.databinding.ActivityMainBinding
import com.example.soundbox.ui.panels.RootMediaPlayerPanel
import com.example.soundbox.ui.panels.RootNavigationBarPanel
import com.realgear.multislidinguppanel.Adapter
import com.realgear.multislidinguppanel.PanelStateListener


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSlidingUpPanel()
    }

    private fun initSlidingUpPanel() {

        val items: MutableList<Class<*>> = arrayListOf(
            RootMediaPlayerPanel::class.java,
            RootNavigationBarPanel::class.java
        )
        // You add your panels here it can be different classes with different layouts
        // but they all should extend the BasePanelView class with same constructors
        // You can add 1 or more then 1 panels
        // You add your panels here it can be different classes with different layouts
        // but they all should extend the BasePanelView class with same constructors
        // You can add 1 or more then 1 panels


        // This is to listen on all the panels you can add methods or extend the class
        // This is to listen on all the panels you can add methods or extend the class
        binding.rootSlidingUpPanel.setPanelStateListener(object :
            PanelStateListener(binding.rootSlidingUpPanel) {


        })
        // The adapter handles the items you can also extend it but I don't recommend for
        // beginners
        // The adapter handles the items you can also extend it but I don't recommend for
        // beginners
        binding.rootSlidingUpPanel.adapter =object : Adapter(this,items){}

        }
    }


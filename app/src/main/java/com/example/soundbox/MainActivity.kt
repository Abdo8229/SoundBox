package com.example.soundbox

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.soundbox.databinding.ActivityMainBinding
import com.example.soundbox.ui.UIThread
import com.example.soundbox.utils.PermissionManager
import com.example.soundbox.views.panels.RootMediaPlayerPanel
import com.example.soundbox.views.panels.RootNavigationBarPanel
import com.realgear.multislidinguppanel.Adapter
import com.realgear.multislidinguppanel.PanelStateListener
import java.net.URI


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var uiThread:UIThread
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        PermissionManager.requestPermission(this,Manifest.permission.MANAGE_EXTERNAL_STORAGE,100)
        PermissionManager.requestPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE,100)
//        if(Build.VERSION.SDK_INT== Build.VERSION_CODES.R){
//            if (!Environment.isExternalStorageManager()){
//                val intent : Intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
//                val uri: Uri? =  Uri.fromParts("package",packageName,null)
//                intent.data = uri
//                startActivity(intent)
//            }
//        }

        uiThread = UIThread(this)

    }

    private fun initSlidingUpPanel() {
       /*** val items: MutableList<Class<*>> = arrayListOf(
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
        ***/}

    }


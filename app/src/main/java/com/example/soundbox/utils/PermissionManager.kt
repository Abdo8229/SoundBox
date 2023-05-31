package com.example.soundbox.utils

import android.app.Activity
import android.content.pm.PackageManager

class PermissionManager {
    companion object {
        fun isPermissionGranted(activity: Activity, permission: String): Boolean {
            return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
            activity.requestPermissions(arrayOf(permission), requestCode)
        }
    }
}
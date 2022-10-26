package com.android.gb.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private val PERMISSIONS_REQUEST_FINE_LOCATION = 1

 fun requestLocationPermission(context: Context, activity: Activity) {
    if (ContextCompat.checkSelfPermission(
            context,
            "android.permission.ACCESS_FINE_LOCATION"
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity, arrayOf("android.permission.ACCESS_FINE_LOCATION"),
            PERMISSIONS_REQUEST_FINE_LOCATION
        )
    }
}

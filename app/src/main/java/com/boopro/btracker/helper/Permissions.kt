package com.boopro.btracker.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object Permissions {

    val REQUIRED_NEARBY_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        println("BuildVErsionS")
        arrayOf(
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    } else {
        println("BuildVErsion" + Build.VERSION.SDK_INT)
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val REQUIRED_MAP_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    const val REQUEST_CODE_PERMISSIONS = 1001
    const val REQUEST_CODE_LOCATION = 1002

    fun allPermissionGranted(context: Context): Boolean {
        for (permission in REQUIRED_NEARBY_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                println("permicheck" +permission+ ContextCompat.checkSelfPermission(context, permission))
                return false
            }
        }

        return true
    }
}
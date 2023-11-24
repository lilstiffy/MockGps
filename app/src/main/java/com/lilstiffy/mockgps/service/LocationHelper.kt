package com.lilstiffy.mockgps.service

import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.lilstiffy.mockgps.MockGpsApp


object LocationHelper {
    private const val REQUEST_CODE = 69
    val DEFAULT_LOCATION = LatLng(56.1711569, 14.8612297)

    fun requestPermissions(activity: ComponentActivity) {
        activity.requestPermissions(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            ), REQUEST_CODE
        )
    }

    fun hasPermission(activity: ComponentActivity): Boolean {
        return ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Geocoding

    fun reverseGeocoding(location: LatLng): String? {
        val geocoder: Geocoder = Geocoder(MockGpsApp.shared.applicationContext)

        return null
    }

    fun geocoding(searchterm: String, result: (LatLng?) -> Unit) {
        val geocoder: Geocoder = Geocoder(MockGpsApp.shared.applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(
                searchterm,
                1
            ) { response ->
                val address = response.firstOrNull()
                if (address == null) {
                    result(null)
                    return@getFromLocationName
                }

                result(LatLng(address.latitude, address.longitude))
            }
        } else {
            val response = geocoder.getFromLocationName(searchterm, 1)
            val address = response?.firstOrNull()

            if (address == null) {
                result(null)
                return
            }

            result(LatLng(address.latitude, address.longitude))
        }
    }
}
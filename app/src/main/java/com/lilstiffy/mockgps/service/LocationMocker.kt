package com.lilstiffy.mockgps.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class LocationMocker(context: Context) {
    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private var isMocking = false
    lateinit var latLng: LatLng
    var listener: LocationListener? = null

    fun toggleMocking() {
        if (isMocking)
            stopMockingLocation()
        else
            startMockingLocation()
    }

    @SuppressLint("MissingPermission")
    private fun startMockingLocation() {
        //Permission already checked before hand.
        listener?.let {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                50L,
                0f,
                it
            )
        }
        if (!isMocking) {
            isMocking = true
            GlobalScope.launch(Dispatchers.IO) {
                mockLocation()
            }
            Log.d("LocationMocker", "Mock location started")
        }
    }

    private fun stopMockingLocation() {
        if (isMocking) {
            isMocking = false
            Log.d("LocationMocker", "Mock location stopped")
        }
    }

    private fun addTestProvider() {
        val providerName = LocationManager.GPS_PROVIDER
        val requiresNetwork = true
        val requiresSatellite = false
        val requiresCell = false
        val hasMonetaryCost = false
        val supportsAltitude = false
        val supportsSpeed = false
        val supportsBearing = false
        val powerRequirement = ProviderProperties.POWER_USAGE_HIGH
        val accuracy = ProviderProperties.ACCURACY_FINE

        locationManager.addTestProvider(
            providerName,
            requiresNetwork,
            requiresSatellite,
            requiresCell,
            hasMonetaryCost,
            supportsAltitude,
            supportsSpeed,
            supportsBearing,
            powerRequirement,
            accuracy
        )
    }

    private suspend fun mockLocation() {
        while (isMocking) {
            val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
                // Set your mock coordinates here
                latitude = latLng.latitude
                longitude = latLng.longitude
                time = Date().time
                accuracy = 50f
                elapsedRealtimeNanos = System.nanoTime()
            }

            addTestProvider()
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            locationManager.setTestProviderLocation(
                LocationManager.GPS_PROVIDER,
                mockLocation
            )

            // Sleep for a duration to simulate location update frequency
            kotlinx.coroutines.delay(1000)
        }
    }
}
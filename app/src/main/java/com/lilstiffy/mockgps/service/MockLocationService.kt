package com.lilstiffy.mockgps.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.lilstiffy.mockgps.storage.StorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MockLocationService : Service() {

    private companion object {
        const val TAG = "MockLocationService"
    }

    var isMocking = false
        private set

    lateinit var latLng: LatLng
    var listener: LocationListener? = null

    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return MockLocationBinder()
    }

    override fun onDestroy() {
        stopMockingLocation()
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    fun toggleMocking() {
        if (isMocking) stopMockingLocation() else startMockingLocation()
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

        // Will be added if not existing.
        StorageManager.addLocationToHistory(latLng)

        if (!isMocking) {
            isMocking = true
            GlobalScope.launch(Dispatchers.IO) {
                mockLocation()
            }
            Log.d(TAG, "Mock location started")
        }
    }

    private fun stopMockingLocation() {
        if (isMocking) {
            isMocking = false
            Log.d(TAG, "Mock location stopped")
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
        addTestProvider()
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)

        while (isMocking) {
            val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
                // Set your mock coordinates here
                latitude = latLng.latitude
                longitude = latLng.longitude
                altitude = 12.5
                time = System.currentTimeMillis()
                accuracy = 10f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }

            locationManager.setTestProviderLocation(
                LocationManager.GPS_PROVIDER,
                mockLocation
            )
            // Sleep for a duration to simulate location update frequency
            kotlinx.coroutines.delay(300L)
        }
    }

    inner class MockLocationBinder : Binder() {
        fun getService(): MockLocationService {
            return this@MockLocationService
        }
    }
}

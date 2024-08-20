package com.lilstiffy.mockgps

import android.app.Application
import com.lilstiffy.mockgps.service.VibratorService
import com.lilstiffy.mockgps.storage.StorageManager

class MockGpsApp : Application() {
    companion object {
        lateinit var shared: MockGpsApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        shared = this
        StorageManager.initialise(this)
        VibratorService.initialise(this)
    }

}
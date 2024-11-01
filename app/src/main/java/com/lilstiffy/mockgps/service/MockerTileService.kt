package com.lilstiffy.mockgps.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.lilstiffy.mockgps.storage.StorageManager

/**
 * Tile service for mocking location
 */
class MockerTileService : TileService() {

    override fun onCreate() {
        super.onCreate()
        StorageManager.initialise(this)
    }

    override fun onClick() {
        super.onClick()
        StorageManager.initialise(this).also {
            MockLocationService.instance?.let { service ->
                service.toggleMocking().also {
                    if (service.isMocking) {
                        qsTile.state = Tile.STATE_ACTIVE
                        qsTile.updateTile()
                    } else {
                        qsTile.state = Tile.STATE_INACTIVE
                        qsTile.updateTile()
                    }
                }
            }
        }
    }
}
package com.lilstiffy.mockgps.ui.screens.viewmodels

import android.location.Address
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.lilstiffy.mockgps.service.LocationHelper
import com.lilstiffy.mockgps.service.MockLocationService
import com.lilstiffy.mockgps.storage.StorageManager

class MapViewModel : ViewModel() {
    var markerPosition: MutableState<LatLng> = mutableStateOf(StorageManager.getLatestLocation())
        private set

    var markerPositionIsFavorite: MutableState<Boolean> = mutableStateOf(false)

    var address: MutableState<Address?> = mutableStateOf(null)
        private set

    fun updateMarkerPosition(latLng: LatLng) {
        markerPosition.value = latLng
        MockLocationService.instance?.latLng = latLng

        LocationHelper.reverseGeocoding(latLng) { foundAddress ->
            address.value = foundAddress
        }

        checkIfFavorite()
    }

    fun toggleFavoriteForLocation() {
        StorageManager.toggleFavoriteForPosition(markerPosition.value)
        checkIfFavorite()
    }

    private fun checkIfFavorite() {
        markerPositionIsFavorite.value = StorageManager.favorites.contains(markerPosition.value)
    }

}
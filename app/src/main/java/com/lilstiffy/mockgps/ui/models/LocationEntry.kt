package com.lilstiffy.mockgps.ui.models

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.lilstiffy.mockgps.extensions.displayString

data class LocationEntry(
    var latLng: LatLng,
    var address: Address?
) {
    fun displayString(): String {
        return address?.displayString() ?: "-"
    }
}

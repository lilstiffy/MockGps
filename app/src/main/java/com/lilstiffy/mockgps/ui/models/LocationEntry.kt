package com.lilstiffy.mockgps.ui.models

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng

@Keep
data class LocationEntry(
    var latLng: LatLng,
    var addressLine: String? = null
)

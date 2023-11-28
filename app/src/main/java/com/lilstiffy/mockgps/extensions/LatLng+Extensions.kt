package com.lilstiffy.mockgps.extensions

import com.google.android.gms.maps.model.LatLng

fun LatLng.prettyPrint(): String {
    return "Latitude: ${this.latitude}\nLongitude: ${this.longitude}"
}
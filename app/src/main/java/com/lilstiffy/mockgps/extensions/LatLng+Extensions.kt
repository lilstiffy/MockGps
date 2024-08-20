package com.lilstiffy.mockgps.extensions

import com.google.android.gms.maps.model.LatLng

fun LatLng.equalTo(other: LatLng): Boolean {
    return (latitude == other.latitude && longitude == other.longitude)
}

fun LatLng.prettyPrint(): String {
    return "Lat: ${this.latitude}\nLng: ${this.longitude}"
}
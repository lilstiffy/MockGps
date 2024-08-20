package com.lilstiffy.mockgps.extensions

import android.location.Address

fun Address.displayString(): String {
    val addressLines = mutableListOf<String>()

    // Concatenate lines of the address
    for (i in 0..maxAddressLineIndex) {
        addressLines.add(getAddressLine(i) ?: "")
    }

    // Concatenate other details
    val otherDetails = mutableListOf<String>().apply {
        locality?.let { add(it) }
        adminArea?.let { add(it) }
        countryName?.let { add(it) }
        postalCode?.let { add(it) }
    }

    // Combine address lines and other details
    val result = addressLines.first().ifEmpty { otherDetails.joinToString(", ") }

    return result.ifEmpty { "No address available" }
}
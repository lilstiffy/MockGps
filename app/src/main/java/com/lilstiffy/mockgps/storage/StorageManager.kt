package com.lilstiffy.mockgps.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lilstiffy.mockgps.service.LocationHelper

object StorageManager {
    private const val PREF = "gpsprefs"
    private const val KEY_HISTORY = "history"
    private const val KEY_FAVORITES = "favorites"

    private lateinit var pref: SharedPreferences

    fun initialise(context: Context) {
        pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    fun getLatestLocation(): LatLng {
        return locationHistory.lastOrNull() ?: LocationHelper.DEFAULT_LOCATION
    }

    fun addLocationToHistory(latLng: LatLng) {
        val tempHistory = locationHistory
        if (!locationHistory.contains(latLng)) {
            tempHistory.add(latLng)
            locationHistory = tempHistory
        }
    }

    fun toggleFavoriteForPosition(latLng: LatLng) {
        if (favorites.contains(latLng))
            removeFavorite(latLng)
        else
            addLocationToFavorites(latLng)
    }

    private fun addLocationToFavorites(latLng: LatLng) {
        val tempFavorites = favorites
        tempFavorites.add(latLng)
        favorites = tempFavorites
    }

    private fun removeFavorite(latLng: LatLng) {
        val tempFavorites = favorites
        tempFavorites.remove(latLng)
        favorites = tempFavorites
    }

    private var locationHistory: MutableList<LatLng>
        get() {
            val json = pref.getString(KEY_HISTORY, "[]")
            val typeToken = object : TypeToken<MutableList<LatLng>>() {}.type
            return Gson().fromJson(json, typeToken)
        }
        private set(value) {
            val json = Gson().toJson(value)
            pref.edit {
                putString(KEY_HISTORY, json)
                commit()
            }
        }

    var favorites: MutableList<LatLng>
        get() {
            val json = pref.getString(KEY_FAVORITES, "[]")
            val typeToken = object : TypeToken<MutableList<LatLng>>() {}.type
            return Gson().fromJson(json, typeToken)
        }
        private set(value) {
            val json = Gson().toJson(value)
            pref.edit {
                putString(KEY_FAVORITES, json)
                commit()
            }
        }

}
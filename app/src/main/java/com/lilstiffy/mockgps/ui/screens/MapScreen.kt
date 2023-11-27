package com.lilstiffy.mockgps.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.lilstiffy.mockgps.MainActivity
import com.lilstiffy.mockgps.service.LocationHelper
import com.lilstiffy.mockgps.storage.StorageManager
import com.lilstiffy.mockgps.ui.components.SearchComponent
import com.lilstiffy.mockgps.ui.theme.ButtonGreen
import com.lilstiffy.mockgps.ui.theme.ButtonRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun MapScreen(activity: MainActivity) {
    var markerPosition by remember { mutableStateOf(StorageManager.getLatestLocation()) }
    var isMocking by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 7f)
    }

    fun animateCameraTo(latLng: LatLng) {
        GlobalScope.launch(Dispatchers.Main) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(markerPosition, 6f, 0f, 0f)
                ),
                durationMs = 1000
            )
        }
    }



    Box(modifier = Modifier.fillMaxSize()){
        // Google maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = {
                LocationHelper.requestPermissions(activity)
                activity.mockLocationService?.latLng = markerPosition
            },
            uiSettings = MapUiSettings(
                tiltGesturesEnabled = false,
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,

            ),
            onMapClick = { latLng ->
                if (!isMocking) {
                    markerPosition = latLng
                    activity.mockLocationService?.latLng = markerPosition
                }
            },
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(markerPosition)
            )
        }

        // Toggle button
        IconToggleButton(
            modifier = Modifier
                .height(72.dp)
                .width(72.dp)
                .padding(8.dp)
                .align(Alignment.BottomEnd),
            checked = isMocking,
            onCheckedChange = { checked ->
                isMocking = activity.toggleMocking() ?: isMocking
            },
            colors = IconButtonDefaults.filledIconToggleButtonColors(
                checkedContainerColor = ButtonRed, containerColor = ButtonGreen, contentColor = Color.White
            )
        ) {
            Row {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(48.dp)
                        .height(48.dp),
                    imageVector = if (isMocking) Icons.Sharp.Close else Icons.Sharp.PlayArrow,
                    contentDescription = "status"
                )
            }
        }

        SearchComponent(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight(0.075f)
                .fillMaxWidth(),
            onSearch = { searchTerm ->
                LocationHelper.geocoding(searchTerm) { foundLatLng ->
                    foundLatLng?.let {
                        markerPosition = it
                        activity.mockLocationService?.latLng = it
                        animateCameraTo(it)
                    }
                }
            }
        )

    }
}

@Preview
@Composable
fun MapScreenPreview() {
    MapScreen(MainActivity())
}
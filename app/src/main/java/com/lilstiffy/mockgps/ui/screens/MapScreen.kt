package com.lilstiffy.mockgps.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.lilstiffy.mockgps.MainActivity
import com.lilstiffy.mockgps.extensions.roundedShadow
import com.lilstiffy.mockgps.service.LocationHelper
import com.lilstiffy.mockgps.ui.components.FooterComponent
import com.lilstiffy.mockgps.ui.components.SearchComponent
import com.lilstiffy.mockgps.ui.screens.viewmodels.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    activity: MainActivity
) {
    var isMocking by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapViewModel.markerPosition.value, 15f)
    }

    fun animateCamera() {
        GlobalScope.launch(Dispatchers.Main) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(mapViewModel.markerPosition.value, 15f, 0f, 0f)
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
                mapViewModel.updateMarkerPosition(mapViewModel.markerPosition.value)
            },
            uiSettings = MapUiSettings(
                tiltGesturesEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false
            ),
            onMapClick = { latLng ->
                if (!isMocking) {
                    mapViewModel.updateMarkerPosition(latLng)
                }
            },
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(mapViewModel.markerPosition.value)
            )
        }

        SearchComponent(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight(0.075f)
                .fillMaxWidth()
                .padding(4.dp)
                .roundedShadow(8.dp)
                .zIndex(32f),
            onSearch = { searchTerm ->
                // We don't want to support switching locations while already mocking
                if (isMocking) {
                    Toast.makeText(activity, "You can't search while mocking location", Toast.LENGTH_SHORT).show()
                    return@SearchComponent
                }

                LocationHelper.geocoding(searchTerm) { foundLatLng ->
                    foundLatLng?.let {
                        mapViewModel.updateMarkerPosition(it)
                        animateCamera()
                    }
                }
            }
        )

        FooterComponent(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(1f)
                .padding(4.dp)
                .zIndex(32f)
                .roundedShadow(8.dp),
            address = mapViewModel.address.value,
            latLng = mapViewModel.markerPosition.value,
            isMocking = isMocking,
            isFavorite = mapViewModel.markerPositionIsFavorite.value,
            onStart = { isMocking = activity.toggleMocking() },
            onFavorite = { mapViewModel.toggleFavoriteForLocation() }
        )

    }
}
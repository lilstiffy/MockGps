package com.lilstiffy.mockgps.ui.screens

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.lilstiffy.mockgps.MainActivity
import com.lilstiffy.mockgps.R
import com.lilstiffy.mockgps.extensions.roundedShadow
import com.lilstiffy.mockgps.service.LocationHelper
import com.lilstiffy.mockgps.storage.StorageManager
import com.lilstiffy.mockgps.ui.components.FavoritesListComponent
import com.lilstiffy.mockgps.ui.components.FooterComponent
import com.lilstiffy.mockgps.ui.components.SearchComponent
import com.lilstiffy.mockgps.ui.screens.viewmodels.MapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    activity: MainActivity,
) {
    val scope = rememberCoroutineScope()

    var isMocking by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapViewModel.markerPosition.value, 15f)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val MapStyle = if (isSystemInDarkTheme())
        MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.style_json)
    else
        MapStyleOptions("")

    fun animateCamera() {
        scope.launch(Dispatchers.Main) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(mapViewModel.markerPosition.value, 15f, 0f, 0f)
                ),
                durationMs = 1000
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = {
                LocationHelper.requestPermissions(activity)
                mapViewModel.updateMarkerPosition(mapViewModel.markerPosition.value)
            },
            properties = MapProperties(
                mapStyleOptions = MapStyle
            ),
            uiSettings = MapUiSettings(
                tiltGesturesEnabled = false,
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = false
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

        Column(
            modifier = Modifier.statusBarsPadding()
        ) {
            SearchComponent(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxHeight(0.075f)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .roundedShadow(32.dp)
                    .zIndex(32f),
                onSearch = { searchTerm ->
                    // We don't want to support switching locations while already mocking
                    if (isMocking) {
                        Toast.makeText(
                            activity,
                            "You can't search while mocking location",
                            Toast.LENGTH_SHORT
                        ).show()
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

            // Favorites button.
            IconButton(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .align(Alignment.End),
                onClick = { showBottomSheet = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Blue, contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "show favorites"
                )
            }
        }

        FooterComponent(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(1f)
                .navigationBarsPadding()
                .padding(4.dp)
                .zIndex(32f)
                .roundedShadow(16.dp),
            address = mapViewModel.address.value,
            latLng = mapViewModel.markerPosition.value,
            isMocking = isMocking,
            isFavorite = mapViewModel.markerPositionIsFavorite.value,
            onStart = { isMocking = activity.toggleMocking() },
            onFavorite = { mapViewModel.toggleFavoriteForLocation() }
        )

        if (showBottomSheet) {
            FavoritesListComponent(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                data = StorageManager.favorites,
                onEntryClicked = { clickedEntry ->
                    if (isMocking) {
                        Toast.makeText(
                            activity,
                            "You can't switch location while mocking",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@FavoritesListComponent
                    }
                    mapViewModel.apply {
                        mapViewModel.updateMarkerPosition(clickedEntry.latLng)
                        scope.launch {
                            sheetState.hide()
                            showBottomSheet = false
                        }
                        animateCamera()
                    }
                }
            )
        }

    }
}
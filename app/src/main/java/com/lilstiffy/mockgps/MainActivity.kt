package com.lilstiffy.mockgps

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.lilstiffy.mockgps.service.LocationHelper
import com.lilstiffy.mockgps.service.MockLocationService
import com.lilstiffy.mockgps.service.VibratorService
import com.lilstiffy.mockgps.storage.StorageManager
import com.lilstiffy.mockgps.ui.screens.MapScreen
import com.lilstiffy.mockgps.ui.theme.ButtonGreen
import com.lilstiffy.mockgps.ui.theme.ButtonRed
import com.lilstiffy.mockgps.ui.theme.MockGpsTheme

class MainActivity : ComponentActivity() {
    var mockLocationService: MockLocationService? = null
    private var isBound = false

    private val locationListener =
        LocationListener { location ->
            Log.d("MainActivity", "Detected new location: $location")
        }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MockLocationService.MockLocationBinder
            mockLocationService = binder.getService()
            mockLocationService?.listener = locationListener
            isBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            isBound = false
        }
    }

    fun toggleMocking(): Boolean {
        if (isBound && LocationHelper.hasPermission(this)) {
            mockLocationService?.toggleMocking()
            if (mockLocationService?.isMocking == true) {
                Toast.makeText(this, "Mocking location...", Toast.LENGTH_SHORT).show()
                VibratorService.vibrate()
                return true
            } else if (mockLocationService?.isMocking == false) {
                Toast.makeText(this, "Stopped mocking location...", Toast.LENGTH_SHORT).show()
                VibratorService.vibrate()
                return false
            }
        }
        else if (!isBound && LocationHelper.hasPermission(this))
            Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "No Location permission", Toast.LENGTH_SHORT).show()

        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MockGpsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapScreen(activity = this)
                }
            }
        }

        // Start the service
        val serviceIntent = Intent(this, MockLocationService::class.java)
        startService(serviceIntent)

        // Bind to the service
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unbind from the service
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MockGpsTheme {
    }
}
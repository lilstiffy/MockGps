package com.lilstiffy.mockgps.ui.components.listitems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lilstiffy.mockgps.extensions.prettyPrint
import com.lilstiffy.mockgps.ui.models.LocationEntry

@Composable
fun FavoriteListItem(
    modifier: Modifier = Modifier,
    locationEntry: LocationEntry,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Location section
        Row {
            Icon(
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Filled.Home,
                tint = Color.Gray,
                contentDescription = "address"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth(0.85f),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                text = locationEntry.addressLine ?: "-"
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Coordinates section
        Row {
            Icon(
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Filled.Place,
                tint = Color.Red,
                contentDescription = "coordinates"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface,
                text = locationEntry.latLng.prettyPrint()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
    }
}
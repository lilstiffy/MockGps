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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lilstiffy.mockgps.extensions.displayString
import com.lilstiffy.mockgps.extensions.prettyPrint
import com.lilstiffy.mockgps.ui.models.LocationEntry
import com.lilstiffy.mockgps.ui.theme.TextBody

@Composable
fun FavoriteListItem(
    modifier: Modifier = Modifier,
    locationEntry: LocationEntry,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Location section
        Row {
            Icon(
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Filled.Home,
                tint = Color.Blue,
                contentDescription = "address"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth(0.85f),
                style = TextBody,
                maxLines = 3,
                text = locationEntry.displayString()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Coordinates section
        Row {
            Icon(
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Filled.Place,
                tint = Color.Blue,
                contentDescription = "coordinates"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                style = TextBody,
                text = locationEntry.latLng.prettyPrint()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
    }
}
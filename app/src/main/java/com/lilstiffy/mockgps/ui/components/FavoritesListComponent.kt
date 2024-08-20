package com.lilstiffy.mockgps.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lilstiffy.mockgps.ui.components.listitems.FavoriteListItem
import com.lilstiffy.mockgps.ui.models.LocationEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesListComponent(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    data: List<LocationEntry>,
    onEntryClicked: (entry: LocationEntry) -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            // Title
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Favorite locations",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // List view
            if (data.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    userScrollEnabled = true
                ) {
                    items(data) {
                        Row {
                            FavoriteListItem(
                                locationEntry = it,
                                onClick = { onEntryClicked(it) }
                            )
                        }
                    }
                }
            } else {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "No favorites have been added",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
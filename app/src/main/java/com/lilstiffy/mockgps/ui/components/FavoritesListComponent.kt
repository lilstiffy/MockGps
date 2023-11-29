package com.lilstiffy.mockgps.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lilstiffy.mockgps.extensions.displayString
import com.lilstiffy.mockgps.ui.components.listitems.FavoriteListItem
import com.lilstiffy.mockgps.ui.models.LocationEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesListComponent(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    data: List<LocationEntry>
) {
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.75f),
        onDismissRequest =  onDismissRequest,
        sheetState = sheetState
    ) {
        Column {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                userScrollEnabled = true
            ) {
                items(data) {
                    Row {
                        FavoriteListItem(
                            locationEntry = it,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}